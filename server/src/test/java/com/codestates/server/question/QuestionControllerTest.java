package com.codestates.server.question;

import com.codestates.server.answer.dto.AnswerDto;
import com.codestates.server.answer.mapper.AnswerMapper;
import com.codestates.server.audit.AuditableResponseDto;
import com.codestates.server.comment.dto.AnswerCommentDto;
import com.codestates.server.comment.dto.QuestionCommentDto;
import com.codestates.server.comment.mapper.AnswerCommentMapper;
import com.codestates.server.comment.mapper.QuestionCommentMapper;
import com.codestates.server.config.SecurityTestConfig;
import com.codestates.server.config.TestUserDetailService;
import com.codestates.server.member.entity.Member;
import com.codestates.server.member.service.MemberService;
import com.codestates.server.question.controller.QuestionController;
import com.codestates.server.question.dto.*;
import com.codestates.server.question.entity.Question;
import com.codestates.server.question.mapper.QuestionMapper;
import com.codestates.server.question.service.QuestionService;
import com.codestates.server.tag.dto.TagResponseDto;
import com.codestates.server.tag.entity.Tag;
import com.codestates.server.tag.service.TagService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestUserDetailService.class})
public class QuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private QuestionMapper questionMapper;
    @MockBean
    private AnswerMapper answerMapper;
    @MockBean
    private AnswerCommentMapper answerCommentMapper;
    @MockBean
    private QuestionService questionService;
    @MockBean
    private TagService tagService;
    @MockBean
    private QuestionCommentMapper questionCommentMapper;
    @MockBean
    private MemberService memberService;


    @Test
    void postQuestionTest() throws Exception {
        //given
        QuestionPostDto post = QuestionPostDto
                .builder()
                .memberId(1L)
                .title("title ?????????")
                .content("content ?????????")
                .categories(List.of("??????1", "??????2"))
                .build();

        QuestionSuccessResponseDto response = new QuestionSuccessResponseDto();
        response.setQuestionId(1L);

        Member member = new Member();
        member.setMemberId(post.getMemberId());

        given(memberService.findMemberByEmail(Mockito.anyString())).willReturn(member);
        given(tagService.findTagsElseCreateTags(Mockito.anyList())).willReturn(new ArrayList<>());
        given(questionMapper.questionPostDtoToQuestion(Mockito.any(QuestionPostDto.class), Mockito.anyList())).willReturn(new Question());
        given(questionService.createQuestion(Mockito.any(Question.class))).willReturn(new Question());
        doNothing().when(tagService).updateQuestionsCount(Mockito.any(Tag.class));
        given(questionMapper.questionToQuestionSuccessResponseDto(Mockito.any(Question.class))).willReturn(response);

        String content = gson.toJson(post);

        //when
        ResultActions actions = mockMvc.perform(
                post("/questions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.questionId").value(1L))
                .andExpect(jsonPath("$.data.message").value("????????? ??????????????? ?????????????????????"))
                .andDo(document(
                        "post-question",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                List.of(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("categories").type(JsonFieldType.ARRAY).description("??????")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("?????? ?????????"),
                                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.message").type(JsonFieldType.STRING).description("?????? ?????????")
                                )
                        )
                ))
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }

    @Test
    void patchQuestionTest() throws Exception {
        //given
        QuestionPatchDto patch = new QuestionPatchDto();
        patch.setQuestionId(1L);
        patch.setMemberId(1L);
        patch.setTitle("????????? ?????? ??????");
        patch.setContent("????????? ?????? ??????");
        patch.setCategories(List.of("???????????????1", "???????????????2"));

        QuestionSuccessResponseDto response = new QuestionSuccessResponseDto();
        response.setQuestionId(1L);

        Member member = new Member();
        member.setMemberId(1L);

        given(memberService.findMemberByEmail(Mockito.anyString())).willReturn(member);
        given(questionMapper.questionPatchDtoToQuestion(Mockito.any(QuestionPatchDto.class))).willReturn(new Question());
        given(questionService.updateQuestion(Mockito.any(Question.class))).willReturn(new Question());
        doNothing().when(tagService).updateQuestionTags(Mockito.any(Question.class), Mockito.anyList());
        given(questionMapper.questionToQuestionSuccessResponseDto(Mockito.any(Question.class))).willReturn(response);

        String content = gson.toJson(patch);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/questions/{question-id}", response.getQuestionId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionId").value(patch.getQuestionId()))
                .andDo(document(
                        "patch-question",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("question-id").description("?????? ?????????")),
                        requestFields(
                                List.of(
                                        fieldWithPath("questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("categories").type(JsonFieldType.ARRAY).description("?????? ??????")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.message").type(JsonFieldType.STRING).description("?????? ?????? ?????? ?????????")
                                )
                        )
                ))
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }

    @Test
    void getQuestionDetailTest() throws Exception {
        //given
        // ?????? ?????? ??????
        AuditableResponseDto auditableResponseDto = new AuditableResponseDto(LocalDateTime.now(), LocalDateTime.now());

        //??????
        TagResponseDto tagResponseDto1 = new TagResponseDto();
        tagResponseDto1.setTagId(1L);
        tagResponseDto1.setCategory("??????1");
        tagResponseDto1.setQuestionsCount(9);

        TagResponseDto tagResponseDto2 = new TagResponseDto();
        tagResponseDto2.setTagId(2L);
        tagResponseDto2.setCategory("??????2");
        tagResponseDto2.setQuestionsCount(9);

        // ?????? ??????
        QuestionCommentDto.Response questionCommentResponseDto = new QuestionCommentDto.Response(
                1L, 1L, 2L, "???????????????", "?????? ???????????????", auditableResponseDto
        );
        AnswerCommentDto.Response answerCommentResponseDto;

        // ?????? ?????? ??????
        List<AnswerDto.Response> answerResponseDtos = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            AnswerDto.Response answerResponseDto = AnswerDto.Response
                    .builder()
                    .answerId(i)
                    .memberId(i + 1L)
                    .questionId(1L)
                    .memberName("????????????????????????" + i)
                    .content("?????? ???????????????")
                    .voteCount(3)
                    .auditableResponseDto(auditableResponseDto)
                    .commentResponseDtos(List.of(new AnswerCommentDto.Response(i, i, i + 7L, "????????????????????????" + i, "?????? ???????????????", auditableResponseDto)))
                    .build();
            answerResponseDtos.add(answerResponseDto);
        }

        // ?????? ?????? ??????
        QuestionDetailResponseDto response = QuestionDetailResponseDto
                .builder()
                .questionResponseDto(
                        QuestionResponseDto.builder()
                                .questionId(1L)
                                .title("?????? ???????????????")
                                .content("?????? ???????????????")
                                .auditableResponseDto(auditableResponseDto)
                                .memberId(1L)
                                .memberName("?????????")
                                .tagResponseDtos(List.of(tagResponseDto1, tagResponseDto2))
                                .answerCount(3)
                                .voteCount(5)
                                .commentResponseDtos(List.of(questionCommentResponseDto))
                                .build()
                )
                .answerResponseDtos(answerResponseDtos)
                .build();

        given(questionService.findQuestion(Mockito.anyLong())).willReturn(new Question());
        given(questionMapper.questionToQuestionDetailResponseDto(
                Mockito.any(Question.class),
                Mockito.any(AnswerMapper.class),
                Mockito.any(AnswerCommentMapper.class),
                Mockito.any(QuestionCommentMapper.class)
        )).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get("/questions/{question-id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.questionResponseDto").value(response.getQuestionResponseDto())) //Todo: ????????? ????????? ?????????
                .andExpect(jsonPath("$.data.answerResponseDtos").isArray())
                .andDo(document(
                        "get-questionDetail",
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("question-id").description("?????? ?????????")),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("?????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.title").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.auditableResponseDto").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data.questionResponseDto.auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data.questionResponseDto.memberId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.memberName").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
                                        fieldWithPath("data.questionResponseDto.tagResponseDtos").type(JsonFieldType.ARRAY).description("????????? ????????? ??????"),
                                        fieldWithPath("data.questionResponseDto.tagResponseDtos[*].tagId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.tagResponseDtos[*].category").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.tagResponseDtos[*].questionsCount").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ??? (?????????)"),
                                        fieldWithPath("data.questionResponseDto.answerCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data.questionResponseDto.voteCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos").type(JsonFieldType.ARRAY).description("????????? ?????? ????????? ??????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].questionId").type(JsonFieldType.NUMBER).description("????????? ?????? ????????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].memberId").type(JsonFieldType.NUMBER).description("????????? ????????? ????????? ?????????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].memberName").type(JsonFieldType.STRING).description("????????? ????????? ????????? ??????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("????????? ????????? ?????? ??????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("data.questionResponseDto.commentResponseDtos[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("data.answerResponseDtos").type(JsonFieldType.ARRAY).description("?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].answerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].memberId").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].memberName").type(JsonFieldType.STRING).description("????????? ????????? ?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].voteCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                        fieldWithPath("data.answerResponseDtos[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data.answerResponseDtos[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos").type(JsonFieldType.ARRAY).description("????????? ?????? ?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].answerId").type(JsonFieldType.NUMBER).description("????????? ??? ?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].memberId").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ?????????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].memberName").type(JsonFieldType.STRING).description("????????? ????????? ?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data.answerResponseDtos[*].commentResponseDtos[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("?????? ????????????"))))
                )
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }

    @Test
    void getQuestionsTest() throws Exception {
        //given

        // ?????? ?????? ??????
        AuditableResponseDto auditableResponseDto = new AuditableResponseDto(LocalDateTime.now(), LocalDateTime.now());

        //??????
        TagResponseDto tagResponseDto1 = new TagResponseDto();
        tagResponseDto1.setTagId(1L);
        tagResponseDto1.setCategory("??????1");
        tagResponseDto1.setQuestionsCount(9);

        TagResponseDto tagResponseDto2 = new TagResponseDto();
        tagResponseDto2.setTagId(2L);
        tagResponseDto2.setCategory("??????2");
        tagResponseDto2.setQuestionsCount(9);

        int page = 1;
        int size = 10;
        long totalElements = 9;

        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            questions.add(new Question());
        }

        Page<Question> pageQuestions = new PageImpl<>(
                questions, PageRequest.of(page - 1, size, Sort.by("questionId").descending()), 1
        );

        List<QuestionResponseDto> responses = new ArrayList<>();
        for (long i = totalElements; i >= totalElements; i--) {
            // ?????? ??????
            QuestionCommentDto.Response questionCommentResponseDto = new QuestionCommentDto.Response(
                    i, i, 1L, "?????????1", "?????? ???????????????", auditableResponseDto
            );

            QuestionResponseDto questionResponseDto = QuestionResponseDto
                    .builder()
                    .questionId(i)
                    .title("?????? ???????????????" + i)
                    .content("?????? ???????????????" + i)
                    .auditableResponseDto(auditableResponseDto)
                    .memberId(i)
                    .memberName("?????????" + i)
                    .tagResponseDtos(List.of(tagResponseDto1, tagResponseDto2))
                    .answerCount(3)
                    .voteCount(5)
                    .commentResponseDtos(List.of(questionCommentResponseDto))
                    .build();
            responses.add(questionResponseDto);
        }

        given(questionService.findQuestions(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).willReturn(pageQuestions);

        given(questionMapper.questionsToQuestionResponseDtos(
                Mockito.anyList(),
                Mockito.any(QuestionCommentMapper.class)
        )).willReturn(responses);

        //when
        String keyword = "";
        String filter = "all";
        String sortedBy = "questionId";
        String order = "descending";

        ResultActions actions = mockMvc.perform(
                get("/questions?page={page}&size={size}&keyword={keyword}&filter={filter}&sortedBy={sortedBy}&order={order}",
                        page, size, keyword, filter, sortedBy, order)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageInfo.page").value(page))
                .andExpect(jsonPath("$.pageInfo.size").value(size))
                .andDo(document(
                        "get-questions",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("????????? ????????? (defaultValue = 1)"),
                                parameterWithName("size").description("???????????? ????????? ??? (defaultValue = 10)"),
                                parameterWithName("keyword").description("?????????"),
                                parameterWithName("filter").description("?????? (defaultValue = all, ???????????? = noAnswer, ???????????? = answer)"),
                                parameterWithName("sortedBy").description("?????? ?????? (defaultValue = questionId, ?????? ??? = voteCount, ?????? ?????? ??? = answerCount)"),
                                parameterWithName("order").description("?????? ????????? ???????????? ?????? ???????????? (defaultValue = descending, ???????????? = ascending)")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                        fieldWithPath("data[*].questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].title").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                                        fieldWithPath("data[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data[*].memberId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????????"),
                                        fieldWithPath("data[*].memberName").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos").type(JsonFieldType.ARRAY).description("????????? ????????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].tagId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].category").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].questionsCount").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ??? (?????????)"),
                                        fieldWithPath("data[*].answerCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data[*].voteCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data[*].commentResponseDtos").type(JsonFieldType.ARRAY).description("????????? ?????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].questionId").type(JsonFieldType.NUMBER).description("????????? ?????? ????????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].memberId").type(JsonFieldType.NUMBER).description("????????? ????????? ????????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].memberName").type(JsonFieldType.STRING).description("????????? ????????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("????????? ????????? ?????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("????????? ??????"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ????????? ??????"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("??? ????????? ??????"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ??????")
                                )
                        )

                ))
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }

    @Test
    void getQuestionsByTagTest() throws Exception {
        //given

        // ?????? ?????? ??????
        AuditableResponseDto auditableResponseDto = new AuditableResponseDto(LocalDateTime.now(), LocalDateTime.now());

        //??????
        TagResponseDto tagResponseDto1 = new TagResponseDto();
        tagResponseDto1.setTagId(1L);
        tagResponseDto1.setCategory("??????1");
        tagResponseDto1.setQuestionsCount(9);

        TagResponseDto tagResponseDto2 = new TagResponseDto();
        tagResponseDto2.setTagId(2L);
        tagResponseDto2.setCategory("??????2");
        tagResponseDto2.setQuestionsCount(9);

        int page = 1;
        int size = 10;
        long totalElements = 9;

        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            questions.add(new Question());
        }

        Page<Question> pageQuestions = new PageImpl<>(
                questions, PageRequest.of(page - 1, size, Sort.by("questionId").descending()), 1
        );

        List<QuestionResponseDto> responses = new ArrayList<>();
        for (long i = totalElements; i >= totalElements; i--) {
            // ?????? ??????
            QuestionCommentDto.Response questionCommentResponseDto = new QuestionCommentDto.Response(
                    i, i, 1L, "?????????1", "?????? ???????????????", auditableResponseDto
            );

            QuestionResponseDto questionResponseDto = QuestionResponseDto
                    .builder()
                    .questionId(i)
                    .title("?????? ???????????????" + i)
                    .content("?????? ???????????????" + i)
                    .auditableResponseDto(auditableResponseDto)
                    .memberId(i)
                    .memberName("?????????" + i)
                    .tagResponseDtos(List.of(tagResponseDto1, tagResponseDto2))
                    .answerCount(3)
                    .voteCount(5)
                    .commentResponseDtos(List.of(questionCommentResponseDto))
                    .build();
            responses.add(questionResponseDto);
        }

        given(tagService.findOptionalTagByCategory(Mockito.anyString())).willReturn(Optional.of(new Tag()));
        given(questionService.findQuestionsByOptionalTag(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Optional.class))).willReturn(pageQuestions);
        given(questionMapper.questionsToQuestionResponseDtos(Mockito.anyList(), Mockito.any(QuestionCommentMapper.class))).willReturn(responses);

        //when
        String category = "??????1";

        ResultActions actions = mockMvc.perform(
                get("/questions/tagged/{category}?page={page}&size={size}",
                        category, page, size)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageInfo.page").value(page))
                .andExpect(jsonPath("$.pageInfo.size").value(size))
                .andDo(document(
                        "get-questionsByTag",
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("category").description("?????? ?????? ?????? (???????????? ?????? ????????? ?????? ?????? ????????? ?????????)")),
                        requestParameters(
                                parameterWithName("page").description("????????? ????????? (defaultValue = 1)"),
                                parameterWithName("size").description("???????????? ????????? ??? (defaultValue = 10)")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                        fieldWithPath("data[*].questionId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].title").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                                        fieldWithPath("data[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                        fieldWithPath("data[*].memberId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????????"),
                                        fieldWithPath("data[*].memberName").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos").type(JsonFieldType.ARRAY).description("????????? ????????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].tagId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].category").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].tagResponseDtos[*].questionsCount").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ??? (?????????)"),
                                        fieldWithPath("data[*].answerCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data[*].voteCount").type(JsonFieldType.NUMBER).description("????????? ?????? ?????? ???"),
                                        fieldWithPath("data[*].commentResponseDtos").type(JsonFieldType.ARRAY).description("????????? ?????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].questionId").type(JsonFieldType.NUMBER).description("????????? ?????? ????????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].memberId").type(JsonFieldType.NUMBER).description("????????? ????????? ????????? ?????????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].memberName").type(JsonFieldType.STRING).description("????????? ????????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto").type(JsonFieldType.OBJECT).description("????????? ????????? ?????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto.createdAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("data[*].commentResponseDtos[*].auditableResponseDto.modifiedAt").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("????????? ??????"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ????????? ??????"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("??? ????????? ??????"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ??????")
                                )
                        )

                ))
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }

    @Test
    void deleteQuestionTest() throws Exception {
        //given
        long questionId = 1L;
        doNothing().when(questionService).deleteQuestion(Mockito.anyLong(), Mockito.anyString());

        //when
        ResultActions actions = mockMvc.perform(
                delete("/questions/{question-id}", questionId)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isNoContent())
                .andDo(document(
                        "delete-question",
                        pathParameters(parameterWithName("question-id").description("?????? ?????????"))
                ))
                .andReturn();

        System.out.println("\nresult = " + result.getResponse().getContentAsString() + "\n");
    }
}
