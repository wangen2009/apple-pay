/*
 * Copyright 2016-2102 Appleframework(http://www.appleframework.com) Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appleframework.pay.controller.sett;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.appleframework.pay.account.service.RpSettQueryService;
import com.appleframework.pay.common.core.page.PageBean;
import com.appleframework.pay.common.core.page.PageParam;
import com.appleframework.pay.common.core.utils.StringUtil;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.controller.common.ConstantClass;
import com.appleframework.pay.controller.common.JSONParam;
import com.appleframework.pay.user.entity.RpUserInfo;

/**
 */
@Controller
@RequestMapping("/merchant/sett")
public class SettController extends BaseController {

	@Autowired
	private RpSettQueryService rpSettQueryService;


	/**
	 * 函数功能说明 ： 查询结算记录
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "/getSettList", method = { RequestMethod.POST, RequestMethod.GET })
	public String getAccountInfo(HttpServletRequest request) {

		return "sett/list";
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/ajaxSettList", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String ajaxPaymentList(HttpServletRequest request, @RequestBody JSONParam[] params) throws IllegalAccessException, InvocationTargetException {
		// convertToMap定义于父类，将参数数组中的所有元素加入一个HashMap
		HashMap<String, String> paramMap = convertToMap(params);
		String sEcho = paramMap.get("sEcho");
		int start = Integer.parseInt(paramMap.get("iDisplayStart"));
		int length = Integer.parseInt(paramMap.get("iDisplayLength"));
		String beginDate = paramMap.get("beginDate");
		String endDate = paramMap.get("endDate");
		if (StringUtil.isEmpty(beginDate) && !StringUtil.isEmpty(endDate)) {
			beginDate = endDate;
		}
		if (StringUtil.isEmpty(endDate) && !StringUtil.isEmpty(beginDate)) {
			endDate = beginDate;
		}
		String merchantRequestNo = paramMap.get("merchantRequestNo");
		String status = paramMap.get("status");
		RpUserInfo userInfo = (RpUserInfo) request.getSession().getAttribute(ConstantClass.USER);

		// 页面当前页需要显示的记录数据
		PageParam pageParam = new PageParam(start / length + 1, length);
		Map<String, Object> settMap = new HashMap<String, Object>();
		settMap.put("userNo", userInfo.getUserNo());
		settMap.put("settStatus", status);
		settMap.put("merchantRequestNo", merchantRequestNo);
		settMap.put("beginDate", beginDate);
		settMap.put("endDate", endDate);
		PageBean pageBean = rpSettQueryService.querySettRecordListPage(pageParam, settMap);
		
		Long count = Long.valueOf(pageBean.getTotalCount() + "");

		String jsonString = JSON.toJSONString(pageBean.getRecordList());
		String json = "{\"sEcho\":" + sEcho + ",\"iTotalRecords\":" + count.longValue() + ",\"iTotalDisplayRecords\":" + count.longValue() + ",\"aaData\":" + jsonString + "}";
		return json;
	}

}
