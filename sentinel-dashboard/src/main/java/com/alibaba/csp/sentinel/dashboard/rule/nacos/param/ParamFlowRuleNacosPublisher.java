package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linjiayong
 * @description 热点规则
 * @date 2021/2/4 10:04
 */
@Component("paramFlowRuleNacosPublisher")
public class ParamFlowRuleNacosPublisher implements DynamicRulePublisher<java.util.List<ParamFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<java.util.List<ParamFlowRuleCorrectEntity>, String> converter;

    @Override
    public void publish(String app, java.util.List<ParamFlowRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        for (ParamFlowRuleEntity rule : rules) {
            if (rule.getApp() == null) {
                rule.setApp(app);
            }
        }
        //  转换
        List<ParamFlowRuleCorrectEntity> list = rules.stream().map(rule -> {
            ParamFlowRuleCorrectEntity entity = new ParamFlowRuleCorrectEntity();
            BeanUtils.copyProperties(rule,entity);
            return entity;
        }).collect(Collectors.toList());
        configService.publishConfig(app + NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX, NacosConfigUtil.GROUP_ID, converter.convert(list));
    }
}