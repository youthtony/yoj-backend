package com.yitao.yoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yitao.yoj.annotation.AuthCheck;
import com.yitao.yoj.common.BaseResponse;
import com.yitao.yoj.common.ErrorCode;
import com.yitao.yoj.common.ResultUtils;
import com.yitao.yoj.constant.UserConstant;
import com.yitao.yoj.exception.BusinessException;
import com.yitao.yoj.model.dto.question.QuestionQueryRequest;
import com.yitao.yoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yitao.yoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yitao.yoj.model.entity.Question;
import com.yitao.yoj.model.entity.QuestionSubmit;
import com.yitao.yoj.model.entity.User;
import com.yitao.yoj.model.vo.QuestionSubmitVO;
import com.yitao.yoj.service.QuestionSubmitService;
import com.yitao.yoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/youthtony">小易</a>
 * @from   
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 题目提交ID
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、代码等信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

}
