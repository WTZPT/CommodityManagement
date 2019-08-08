package com.jesper.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.jesper.mapper.UserMapper;
import com.jesper.model.User;

import com.mongodb.util.JSON;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.Random;

/**
 * 用户管理
 */
@Controller
public class UserController {
    private static ThreadLocal<String> code = new ThreadLocal<>();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JavaMailSender mailSender; //自动注入的Bean

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数

    /**
     * 登录跳转
     * @param model 请求交互传递的数据模型
     * @return 登录页面
     */
    @GetMapping("/user/login")
    public String loginGet(Model model) {
        return "login";
    }

    /**
     * 登录请求
     * @param model 请求交互传递的数据模型
     * @param user  前台用户登陆的数据
     * @return  if(true)：跳转dashboard页面  else  返回登陆页面并报错
     */
    @PostMapping("/user/login")
    public String loginPost(User user, Model model) {
//        String userInfo = request.getParameter("userInfo");
//        User user = JSONObject.parseObject(userInfo, User.class);
        User user1 = userMapper.selectByNameAndPwd(user);
        if (user1 != null) {
            httpSession.setAttribute("user", user1);
            User name = (User) httpSession.getAttribute("user");
            return "redirect:dashboard";
        } else {
            model.addAttribute("error", "用户名或密码错误，请重新登录！");
            return "login";
        }
    }


    /**
     * 注册跳转
     * @param model 请求交互传递的数据模型
     * @return  注册页面
     */
    @GetMapping("/user/register")
    public String register(Model model) {
        return "register";
    }


    /**
     * 注册
     * @param model 请求交互传递的数据模型
     * @return
     */
    @PostMapping("/user/register")
    public String registerPost(User user, Model model) {
        System.out.println("用户名" + user.getUserName());
        try {
            userMapper.selectIsName(user);
            model.addAttribute("error", "该账号已存在！");
        } catch (Exception e) {
            Date date = new Date();
            user.setAddDate(date);
            user.setUpdateDate(date);
            userMapper.insert(user);
            System.out.println("注册成功");
            model.addAttribute("error", "恭喜您，注册成功！");
            return "login";
        }
        return "register";
    }

    /**
     * 登录跳转
     *
     * @param model 请求交互传递的数据模型
     * @return
     */
    @GetMapping("/user/forget")
    public String forgetGet(Model model) {
        return "forget";
    }


    /**
     * 登录
     *
     * @param
     * @param model 请求交互传递的数据模型
     * @param
     * @return
     */
    @PostMapping("/user/forget")
//    @RequestMapping(value = "/user/forget", method = RequestMethod.POST)
//    @ResponseBody
    @ResponseBody
    public String forgetPost(@RequestBody String jsonObject, Model model) {
        User user = JSONObject.parseObject(jsonObject, User.class);
        String password = userMapper.selectPasswordByName(user);
        model.addAttribute("user", user);
        if (password == null) {
            model.addAttribute("error", "帐号不存在或邮箱不正确！");
            model.addAttribute("send", "failed");
            return "failed";
        } else {
            model.addAttribute("error", "验证码已发到您的邮箱,请查收！");
            String codeStr = getCode(6);
            code.set(codeStr);
            String email = user.getEmail();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(Sender);
            message.setTo(email); //接收者邮箱
            message.setSubject("YX后台信息管理系统-密码找回");
            StringBuilder sb = new StringBuilder();
            sb.append(user.getUserName() + "用户您好！您的注册密码是：" + codeStr + "。感谢您使用YX信息管理系统！");
            message.setText(sb.toString());
            mailSender.send(message);
            model.addAttribute("send", "success");
            return codeStr;
        }
    }



    @RequestMapping(value = "/user/modifyPasswd", method = RequestMethod.POST)
    @ResponseBody
    public String modifyPassword(@RequestBody String jsonObject, Model model) {
        User user = JSONObject.parseObject(jsonObject, User.class);
        model.addAttribute("user", user);
        int update = userMapper.updatePasswd(user);
        if (update == 1) {
            return "success";
        }else
            return "failed";
    }

    /**
     * 获取用户邮箱收到的验证码
     * @return 邮箱验证码
     */
//    @GetMapping("/user/emailCode")
    @ResponseBody
    @RequestMapping(value = "/user/emailCode", method = RequestMethod.GET)
    public String getEmailCode(Model model) {
        return String.valueOf(code.get()) ;
    }

    /*
     * 定义一个获取随机验证码的方法：getCode();
     */
    public static String getCode(int n) {
        String string = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";//保存数字0-9 和 大小写字母
        char[] ch = new char[n]; //声明一个字符数组对象ch 保存 验证码
        for (int i = 0; i < n; i++) {
            Random random = new Random();//创建一个新的随机数生成器
            int index = random.nextInt(string.length());//返回[0,string.length)范围的int值    作用：保存下标
            ch[i] = string.charAt(index);//charAt() : 返回指定索引处的 char 值   ==》保存到字符数组对象ch里面
        }
        //将char数组类型转换为String类型保存到result
        //String result = new String(ch);//方法一：直接使用构造方法      String(char[] value) ：分配一个新的 String，使其表示字符数组参数中当前包含的字符序列。
        String result = String.valueOf(ch);//方法二： String方法   valueOf(char c) ：返回 char 参数的字符串表示形式。
        return result;
    }


    /**
     *
     * @param model 请求交互传递的数据模型
     * @return
     */
    @GetMapping("/user/userManage")
    public String userManageGet(Model model) {
        User user = (User) httpSession.getAttribute("user");
        User user1 = userMapper.selectByNameAndPwd(user);
        model.addAttribute("user", user1);
        return "user/userManage";
    }


    /**
     *
     * @param model 请求交互传递的数据模型
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/user/userManage")
    public String userManagePost(Model model, User user, HttpSession httpSession) {
        Date date = new Date();
        user.setUpdateDate(date);
        int i = userMapper.update(user);
        httpSession.setAttribute("user",user);
        return "redirect:userManage";
    }

    @GetMapping("/user/loginOut")
    public String loginOut(Model model){
        httpSession.removeAttribute("user");
        model.addAttribute("error", "退出成功");
        return "redirect:login";
    }

}
