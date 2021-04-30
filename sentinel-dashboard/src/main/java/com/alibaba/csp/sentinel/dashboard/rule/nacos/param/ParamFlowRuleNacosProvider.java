package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author linjiayong
 * @description 热点规则
 * @date 2021/2/4 10:04
 */
@Component("paramFlowRuleNacosProvider")
public class ParamFlowRuleNacosProvider implements DynamicRuleProvider<java.util.List<ParamFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<String, java.util.List<ParamFlowRuleCorrectEntity>> converter;

    @Override
    public java.util.List<ParamFlowRuleEntity> getRules(String appName) throws Exception {
        String rules = configService.getConfig(appName + NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX, NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        java.util.List<ParamFlowRuleCorrectEntity> entityList = converter.convert(rules);
        entityList.forEach(e -> e.setApp(appName));
        return entityList.stream().map(rule -> {
            ParamFlowRule paramFlowRule = new ParamFlowRule();
            BeanUtils.copyProperties(rule, paramFlowRule);
            ParamFlowRuleEntity entity = ParamFlowRuleEntity.fromAuthorityRule(rule.getApp(), rule.getIp(), rule.getPort(), paramFlowRule);
            entity.setId(rule.getId());
            entity.setGmtCreate(rule.getGmtCreate());
            return entity;
        }).collect(Collectors.toList());
    }
}