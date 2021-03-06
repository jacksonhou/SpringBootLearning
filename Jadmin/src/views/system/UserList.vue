<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item>
          <el-input placeholder="用户名" v-model="listQuery.userName"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="el-icon-refresh" v-if="hasPermission('system:user')" @click.native.prevent="getUserList">查询
          </el-button>
          <el-button type="primary" icon="el-icon-plus" v-if="hasPermission('user:add')" @click.native.prevent="showAddUserDialog">增加
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="userList" v-loading.body="listLoading" element-loading-text="loading" border fit highlight-current-row>
      <el-table-column label="#" align="center" width="80">
        <template slot-scope="scope">
          <span v-text="getIndex(scope.$index)"></span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="创建时间" align="center" prop="registerTime">
        <template slot-scope="scope">
          {{ unix2CurrentTime(scope.row.registerTime) }}
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" align="center" prop="loginTime">
        <template slot-scope="scope">
          {{ unix2CurrentTime(scope.row.loginTime) }}
        </template>
      </el-table-column>
      <el-table-column label="角色" align="center" prop="roleName" :filters="filterRoleNameList" :filter-method="filterRoleName">
        <template slot-scope="scope">
          <el-tag type="success">{{ scope.row.roleName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" v-if="hasPermission('role:update') || hasPermission('user:delete')">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" icon="el-icon-edit-outline" v-if="hasPermission('role:update') && scope.row.id !== userId" @click.native.prevent="showUpdateUserDialog(scope.$index)">修改
          </el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" v-if="hasPermission('user:delete') && scope.row.id !== userId" @click.native.prevent="removeUser(scope.$index)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background class="pull-right" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page" :page-size="listQuery.size" :total="total" :page-sizes="[10, 30, 50, 100]" layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form status-icon class="small-space" label-position="left" label-width="75px" style="width: 300px; margin-left:50px;" :model="tmpUser" :rules="createRules" ref="tmpUser">
        <el-form-item label="邮箱" prop="email" required>
          <el-input type="text" prefix-icon="el-icon-message" auto-complete="off" :readonly="readonly" v-model="tmpUser.email" />
        </el-form-item>
        <el-form-item label="用户名" prop="username" required>
          <el-input type="text" prefix-icon="el-icon-edit" auto-complete="off" :readonly="readonly" v-model="tmpUser.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogStatus === 'add'" required>
          <el-input type="password" prefix-icon="el-icon-edit" auto-complete="off" :readonly="readonly" v-model="tmpUser.password" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select placeholder="please select" v-model="tmpUser.roleId">
            <el-option v-for="item in allRole" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="danger" v-if="dialogStatus === 'add'" @click="$refs['tmpUser'].resetFields()">重置
        </el-button>
        <el-button type="success" v-if="dialogStatus === 'add'" :loading="btnLoading" @click.native.prevent="addUser">创建
        </el-button>
        <el-button type="primary" v-else :loading="btnLoading" @click.native.prevent="updateUserRole">修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { list as getUserList, register, remove } from '@/api/user'
import { list as getRoleList, updateUserRole } from '@/api/role'
import { validateEmail as isValidateEmail } from '@/utils/validate'
import { unix2CurrentTime } from '@/utils'
import { mapGetters } from 'vuex'

export default {
  created() {
    if (this.hasPermission('role:update')) {
      this.getAllRole()
    }
    if (this.hasPermission('system:user')) {
      this.getUserList()
    }
  },
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!isValidateEmail(value)) {
        callback(new Error('邮箱格式错误'))
      } else {
        callback()
      }
    }
    const validateUsername = (rule, value, callback) => {
      if (value.length < 3) {
        callback(new Error('用户名必须超过3个字符'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码必须超过6个字符'))
      } else {
        callback()
      }
    }
    return {
      userList: [], // 用户列表
      allRole: [], // 全部角色
      filterRoleNameList: [], // 用于过滤表格角色的列表 http://element-cn.eleme.io/#/zh-CN/component/table#shai-xuan
      listLoading: false, // 数据加载等待动画
      total: 0, // 数据总数
      listQuery: {
        page: 1, // 页码
        size: 30, // 每页数量
        userName: ''
      },
      dialogStatus: 'add',
      dialogFormVisible: false,
      textMap: {
        update: '修改用户',
        add: '创建用户'
      },
      btnLoading: false, // 按钮等待动画
      readonly: false, // 只读输入框
      tmpUser: {
        id: '',
        email: '',
        username: '',
        password: '',
        roleId: 2 // 对应后端数据库普通用户角色Id
      },
      createRules: {
        email: [{ required: true, trigger: 'blur', validator: validateEmail }],
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      }
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  methods: {
    unix2CurrentTime,
    /**
     * 获取所有角色
     */
    getAllRole() {
      getRoleList().then(response => {
        this.allRole = response.data.list
      })
    },
    /**
     * 获取用户列表
     */
    getUserList() {
      this.listLoading = true
      getUserList(this.listQuery).then(response => {
        this.userList = response.data.list
        this.total = response.data.total
        for (let i = 0; i < this.userList.length; i++) {
          const filterObject = {}
          filterObject.text = this.userList[i].roleName.split('_')[1] // ROLE_ABC
          filterObject.value = this.userList[i].roleName
          this.filterRoleNameList.push(filterObject)
        }
        this.listLoading = false
      })
    },
    /**
     * 改变每页数量
     * @param size 页大小
     */
    handleSizeChange(size) {
      this.listQuery.size = size
      this.listQuery.page = 1
      this.getUserList()
    },
    /**
     * 改变页码
     * @param page 页号
     */
    handleCurrentChange(page) {
      this.listQuery.page = page
      this.getUserList()
    },
    /**
     * 表格序号
     * 可参考自定义表格序号
     * http://element-cn.eleme.io/#/zh-CN/component/table#zi-ding-yi-suo-yin
     * @param index 数据下标
     * @returns 表格序号
     */
    getIndex(index) {
      return (this.listQuery.page - 1) * this.listQuery.size + index + 1
    },
    filterRoleName(value, row) {
      return row.roleName === value
    },
    /**
     * 显示添加用户对话框
     */
    showAddUserDialog() {
      // 显示新增对话框
      this.dialogFormVisible = true
      this.dialogStatus = 'add'
      this.tmpUser.email = ''
      this.tmpUser.username = ''
      this.tmpUser.password = ''
      this.readonly = false
    },
    /**
     * 添加用户
     */
    addUser() {
      this.$refs.tmpUser.validate(valid => {
        if (valid && this.isUniqueInfo(this.tmpUser)) {
          this.btnLoading = true
          register(this.tmpUser).then(() => {
            this.$message.success('创建用户成功！')
            this.getUserList()
            this.dialogFormVisible = false
            this.btnLoading = false
          })
        }
      })
    },
    /**
     * 显示修改用户对话框
     * @param index 用户下标
     */
    showUpdateUserDialog(index) {
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
      this.tmpUser.id = this.userList[index].id
      this.tmpUser.email = this.userList[index].email
      this.tmpUser.username = this.userList[index].username
      this.tmpUser.password = ''
      this.tmpUser.roleId = this.userList[index].roleId
      this.readonly = true
    },
    /**
     * 更新用户角色
     */
    updateUserRole() {
      updateUserRole(this.tmpUser).then(() => {
        this.$message.success('修改用户成功！')
        this.getUserList()
        this.dialogFormVisible = false
      })
    },
    /**
     * 用户资料是否唯一
     * @param user 用户
     */
    isUniqueInfo(user) {
      for (let i = 0; i < this.userList.length; i++) {
        if (this.userList[i].username === user.username) {
          this.$message.error('用户名已经存在！')
          return false
        }
        if (this.userList[i].email === user.email) {
          this.$message.error('邮箱已经存在！')
          return false
        }
      }
      return true
    },
    /**
     * 删除用户
     * @param index 用户下标
     */
    removeUser(index) {
      this.$confirm('确认删除此用户', 'Waring', {
        confirmButtonText: '确认',
        showCancelButton: false,
        type: 'warning'
      }).then(() => {
        const id = this.userList[index].id
        remove(id).then(() => {
          this.$message.success('删除用户成功！')
          this.getUserList()
        })
      })
    }
  }
}

</script>
