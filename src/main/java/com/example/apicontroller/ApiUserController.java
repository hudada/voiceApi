package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.bean.BaseBean;
import com.example.bean.SongBean;
import com.example.bean.UserBean;
import com.example.dao.SongDao;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/user")
public class ApiUserController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private SongDao songDao;
	
	@Value("${bs.allPath}")
	private String location;
	@Autowired
	private ResourceLoader resourceLoader;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public BaseBean<UserBean> add(HttpServletRequest request) {
		UserBean userBean = new UserBean();
		userBean.setUserName(request.getParameter("name"));
		userBean.setPwd(request.getParameter("pwd"));
		userBean.setAge(Integer.parseInt(request.getParameter("age")));
		if (userDao.findUserByUserName(userBean.getUserName()) == null) {
			return ResultUtils.resultSucceed(userDao.save(userBean));
		} else {
			return ResultUtils.resultError("该账号已存在！");
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public BaseBean<UserBean> userLogin(HttpServletRequest request) {
		UserBean userBean = new UserBean();
		userBean.setUserName(request.getParameter("name"));
		userBean.setPwd(request.getParameter("pwd"));
		UserBean select = userDao.findUserByUserNameAndPwd(userBean.getUserName(), userBean.getPwd());
		if (select == null) {
			return ResultUtils.resultError("账号或密码错误");
		} else {
			return ResultUtils.resultSucceed(select);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BaseBean<List<SongBean>> getUser(@PathVariable String id) {
		List<SongBean> list = songDao.findByUid(Long.parseLong(id));
		for (SongBean songBean : list) {
			songBean.setUname(userDao.findOne(songBean.getUid()).getUserName());
		}
		return ResultUtils.resultSucceed(list);
	}

	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<UserBean> add(@RequestParam("file") MultipartFile file,
			HttpServletRequest request) {

		String uid = request.getParameter("uid");
		if (!file.isEmpty()) {
			try {
				String path = uid + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];

				File root = new File(location);
				if (!root.exists()) {
					root.mkdirs();
				}
				Files.copy(file.getInputStream(), Paths.get(location, path));

				UserBean bean = userDao.findOne(Long.parseLong(uid));
				bean.setImg("/head/" + path);
				return ResultUtils.resultSucceed(userDao.save(bean));
			} catch (IOException | RuntimeException e) {
				return ResultUtils.resultError("");
			}
		} else {
			return ResultUtils.resultError("");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/head/{filename:.+}")
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(location, filename).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}
