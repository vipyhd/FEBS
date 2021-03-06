package cc.mrbird.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.system.domain.SysLog;
import cc.mrbird.system.service.LogService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service("logService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl extends BaseService<SysLog> implements LogService {

	@Override
	public List<SysLog> findAllLogs(SysLog log) {
		try {
			Example example = new Example(SysLog.class);
			Criteria criteria = example.createCriteria();
			if (StringUtils.isNotBlank(log.getUsername())) {
				criteria.andCondition("lower(username)=", log.getUsername().toLowerCase());
			}
			if (StringUtils.isNotBlank(log.getOperation())) {
				criteria.andCondition("operation like", "%" + log.getOperation() + "%");
			}
			if (StringUtils.isNotBlank(log.getTimeField())) {
				String[] timeArr = log.getTimeField().split("~");
				criteria.andCondition("to_char(CREATE_TIME,'yyyy-mm-dd') >=", timeArr[0]);
				criteria.andCondition("to_char(CREATE_TIME,'yyyy-mm-dd') <=", timeArr[1]);
			}
			example.setOrderByClause("create_time desc");
			return this.selectByExample(example);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	@Transactional
	public void deleteLogs(String logIds) {
		List<String> list = Arrays.asList(logIds.split(","));
		this.batchDelete(list, "id", SysLog.class);
	}

}
