package cn.edu.bjtu.weibo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.edu.bjtu.weibo.service.RegisterService;


@Controller
public class RegisterController {
	
	 @Autowired
	 RegisterService registerService;
	
	
	 /**
	  * 接受用户注册信息跳转页面,注册成功返回登录界面，失败则停留在注册界面
	  * @param userName
	  * @param password
	  * @return
	  */
	@RequestMapping(value="/signup",method={RequestMethod.GET, RequestMethod.POST})
	public String  addUser(String userName,String password) {

	  boolean flag= registerService.registerNewUser(userName,password);
	  
	  if(flag){    
		  return "login";
	  }else{
		  return "register";
	  }
	}
}
