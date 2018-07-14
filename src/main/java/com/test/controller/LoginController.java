package com.test.controller;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.test.model.Role;
import com.test.model.User;
import com.test.repository.UserRepository;
import com.test.service.UserService;

@Controller
public class LoginController 
{
	
	@Autowired
	private UserService userService;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult
					.rejectValue("email", "error.user",
							"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
			
		}
		return modelAndView;
	}
	
	/*Login*/
	@RequestMapping(value="/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();

		this.assignUsersToModelView(modelAndView);
		
		return modelAndView;
	}
	
	/*redirect ke create view*/
	
	@RequestMapping(value="admin/create", method = RequestMethod.GET)
	public ModelAndView create()
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/create");
		return modelAndView;
	}
	
	

	/*Delete User*/
	
	@RequestMapping(path = "delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteUser(@PathVariable(name = "id") int userId)
	{
		userService.deleteUser(userId);
		ModelAndView modelAndView = new ModelAndView();
		this.assignUsersToModelView(modelAndView);
		return modelAndView;
	}

	
	private void assignUsersToModelView(ModelAndView modelAndView ) 
	{
		String role = "";
		List<User> users = userService.findAll();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) auth.getAuthorities();
		for(int i =0;i<authorities.size();i++)
		{
			role =  authorities.toArray()[i] + "";
			if(role.equals("ADMIN")){
				modelAndView.addObject("adminMessage","Halo Admin");
				modelAndView.addObject("users", users);
				modelAndView.setViewName("admin/home");
				
			}else if(role.equals("USER"))
			{
				modelAndView.addObject("adminMessage","Halo User");
				modelAndView.setViewName("admin/user_home");
			}
			else {
				modelAndView.addObject("AdminMessage","Halo SU");
				modelAndView.setViewName("admin/super_home");
			}
		}
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
	}

    @RequestMapping(path = "/admin/edit/{id}", method = RequestMethod.GET)
    public String editUser(Model model, @PathVariable(value = "id")Integer userId) 
    {
    	User user =  userService.findOne(userId);
        model.addAttribute("user",user);
        return "admin/edit";
    }
    
    @RequestMapping(path = "/admin/lihat/{id}", method = RequestMethod.GET)
    public String lihatUser(Model model, @PathVariable(value = "id")Integer userId) 
    {
    	User user =  userService.findOne(userId);
        model.addAttribute("user",user);
        return "admin/edit";
    }
    
    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public ModelAndView saveProduct(User user) 
    {
    	User editedUser = userService.findOne(user.getId());
    	editedUser.setName(user.getName());
    	editedUser.setEmail(user.getEmail());
    	editedUser.setName(user.getName());
    	editedUser.setLastName(user.getLastName());
        userService.saveSimpleUser(editedUser);
        return this.home();
    }
    
}