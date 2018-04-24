package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping(value = "/api/like")
public class ApiLikeController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private SongDao songDao;
	@Autowired
	private LikeDao likeDao;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<LikeBean> add(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		String sid = request.getParameter("sid");
		LikeBean bean = new LikeBean();
		bean.setUid(Long.parseLong(uid));
		bean.setSid(Long.parseLong(sid));
		likeDao.save(bean);
		SongBean songBean = songDao.findOne(Long.parseLong(sid));
		songBean.setLikeSum(songBean.getLikeSum() + 1);
		songDao.save(songBean);
		return ResultUtils.resultSucceed("");
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public BaseBean<List<SongBean>> find(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		List<LikeBean> bean = likeDao.findByUidAndLikeUid(Long.parseLong(uid));
		List<SongBean> data = new ArrayList<>();
		for (LikeBean likeBean : bean) {
			SongBean bean2 = songDao.findOne(likeBean.getSid());
			bean2.setUname(userDao.findOne(bean2.getUid()).getUserName());
			bean2.setLike(true);
			data.add(bean2);
		}
		return ResultUtils.resultSucceed(data);
	}

	@RequestMapping(value = "/del", method = RequestMethod.POST)
	public BaseBean<LikeBean> del(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		String sid = request.getParameter("sid");
		try {
			SongBean songBean = songDao.findOne(Long.parseLong(sid));
			songBean.setLikeSum(songBean.getLikeSum() - 1);
			songDao.save(songBean);
			likeDao.delete(likeDao.findByUid(Long.parseLong(uid), Long.parseLong(sid)));
			return ResultUtils.resultSucceed("");
		} catch (Exception e) {
			return ResultUtils.resultError("");
		}
	}

}
