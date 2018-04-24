package com.example.apicontroller;

import static org.mockito.Matchers.booleanThat;

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
import com.example.bean.LikeBean;
import com.example.bean.SongBean;
import com.example.bean.UserBean;
import com.example.dao.LikeDao;
import com.example.dao.SongDao;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/song")
public class ApiSongController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private SongDao songDao;
	@Autowired
	private LikeDao likeDao;

	@Value("${bs.allPath}")
	private String location;
	@Autowired
	private ResourceLoader resourceLoader;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<SongBean> add(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

		String uid = request.getParameter("uid");
		String name = request.getParameter("name");
		String length = request.getParameter("length");
		String type = request.getParameter("type");
		if (!file.isEmpty()) {
			try {
				String path = uid + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];

				File root = new File(location);
				if (!root.exists()) {
					root.mkdirs();
				}
				Files.copy(file.getInputStream(), Paths.get(location, path));

				SongBean bean = new SongBean();
				bean.setUid(Long.parseLong(uid));
				bean.setName(name);
				bean.setLength(Long.parseLong(length));
				bean.setAddr("/voice/" + path);
				bean.setType(Integer.parseInt(type));
				bean.setLikeSum(0);
				return ResultUtils.resultSucceed(songDao.save(bean));
			} catch (IOException | RuntimeException e) {
				return ResultUtils.resultError("");
			}
		} else {
			return ResultUtils.resultError("");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/voice/{filename:.+}")
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(location, filename).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public BaseBean<List<SongBean>> list(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		List<SongBean> list = songDao.findAll();
		for (SongBean songBean : list) {
			songBean.setUname(userDao.findOne(songBean.getUid()).getUserName());
			LikeBean bean = likeDao.findByUid(Long.parseLong(uid), songBean.getId());
			if (bean == null) {
				songBean.setLike(false);
			} else {
				songBean.setLike(true);
			}
		}
		return ResultUtils.resultSucceed(list);
	}

	@RequestMapping(value = "/rank", method = RequestMethod.GET)
	public BaseBean<List<SongBean>> rank(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		String type = request.getParameter("type");
		List<SongBean> list = songDao.findByType(Integer.parseInt(type));
		for (SongBean songBean : list) {
			songBean.setUname(userDao.findOne(songBean.getUid()).getUserName());
			if (likeDao.findByUid(Long.parseLong(uid), songBean.getId()) == null) {
				songBean.setLike(false);
			} else {
				songBean.setLike(true);
			}
		}
		return ResultUtils.resultSucceed(list);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public BaseBean<List<SongBean>> search(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		String key = request.getParameter("key");
		List<SongBean> list = songDao.findByKey(key);
		for (SongBean songBean : list) {
			songBean.setUname(userDao.findOne(songBean.getUid()).getUserName());
			if (likeDao.findByUid(Long.parseLong(uid), songBean.getId()) == null) {
				songBean.setLike(false);
			} else {
				songBean.setLike(true);
			}
		}
		return ResultUtils.resultSucceed(list);
	}
}
