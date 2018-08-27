package com.jadmin.admin.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jadmin.admin.system.core.jwt.JwtUtil;
import com.jadmin.admin.system.core.response.Result;
import com.jadmin.admin.system.core.response.ResultGenerator;
import com.jadmin.admin.system.model.User;
import com.jadmin.admin.system.service.UserService;
import com.jadmin.admin.system.service.impl.UserDetailsServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController
{
    @Resource
    private UserService userService;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private JwtUtil jwtUtil;

    @PostMapping
    public Result register(@RequestBody @Valid final User user, final BindingResult bindingResult)
    {
        // {"username":"123456", "password":"123456", "email": "123456@qq.com"}
        if (bindingResult.hasErrors())
        {
            //noinspection ConstantConditions
            final String msg = bindingResult.getFieldError().getDefaultMessage();
            return ResultGenerator.genFailedResult(msg);
        }
        else
        {
            this.userService.save(user);
            return this.getToken(user);
        }
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable final Long id)
    {
        this.userService.deleteById(id);
        return ResultGenerator.genOkResult();
    }

    @PostMapping("/password")
    public Result validatePassword(@RequestBody final User user)
    {
        final User oldUser = this.userService.findById(user.getId());
        final boolean isValidate = this.userService.verifyPassword(user.getPassword(), oldUser.getPassword());
        return ResultGenerator.genOkResult(isValidate);
    }

    @PutMapping
    public Result update(@RequestBody final User user)
    {
        this.userService.update(user);
        return this.getToken(this.userService.findById(user.getId()));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable final Long id)
    {
        final User user = this.userService.findById(id);
        return ResultGenerator.genOkResult(user);
    }

    @GetMapping("/info")
    public Result info(final Principal user)
    {
        final User userDB = this.userService.findDetailByUsername(user.getName());
        return ResultGenerator.genOkResult(userDB);
    }

    @ApiOperation(value = "用户列表获取接口", notes = "可指定页码及每页条数", produces = "application/json")
    @PreAuthorize("hasAuthority('system:user')")
    @GetMapping
    public Result list(@ApiParam(value = "第几页") @RequestParam(defaultValue = "0") final Integer page,
            @ApiParam(value = "每页条数") @RequestParam(defaultValue = "0") final Integer size,
            @ApiParam(value = "用户名") @RequestParam(defaultValue = "") final String userName)
    {
        PageHelper.startPage(page, size);
        final List<User> list = this.userService.findAllUserWithRole(userName);
        //noinspection unchecked
        final PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genOkResult(pageInfo);
    }

    @ApiOperation(value = "用户登录接口", notes = "登录", produces = "application/json")
    @PostMapping("/login")
    public Result login(@ApiParam(value = "用户信息") @RequestBody final User user)
    {
        // {"username":"admin", "password":"admin123"}
        // {"email":"admin@qq.com", "password":"admin123"}
        if (user.getUsername() == null && user.getEmail() == null)
        {
            return ResultGenerator.genFailedResult("username or email empty");
        }
        if (user.getPassword() == null)
        {
            return ResultGenerator.genFailedResult("password empty");
        }
        // 用户名登录
        User dbUser = null;
        if (user.getUsername() != null)
        {
            dbUser = this.userService.findBy("username", user.getUsername());
            if (dbUser == null)
            {
                return ResultGenerator.genFailedResult("username error");
            }
        }
        // 邮箱登录
        if (user.getEmail() != null)
        {
            dbUser = this.userService.findBy("email", user.getEmail());
            if (dbUser == null)
            {
                return ResultGenerator.genFailedResult("email error");
            }
            user.setUsername(dbUser.getUsername());
        }
        // 验证密码
        //noinspection ConstantConditions
        if (!this.userService.verifyPassword(user.getPassword(), dbUser.getPassword()))
        {
            return ResultGenerator.genFailedResult("password error");
        }
        // 更新登录时间
        this.userService.updateLoginTimeByUsername(user.getUsername());
        return this.getToken(user);
    }

    @GetMapping("/logout")
    public Result logout(final Principal user)
    {
        return ResultGenerator.genOkResult();
    }

    /**
     * 获得 token
     */
    private Result getToken(final User user)
    {
        final String username = user.getUsername();
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        final String token = this.jwtUtil.sign(username, userDetails.getAuthorities());
        return ResultGenerator.genOkResult(token);
    }
}
