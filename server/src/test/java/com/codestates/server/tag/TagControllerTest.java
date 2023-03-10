package com.codestates.server.tag;

import com.codestates.server.config.SecurityTestConfig;
import com.codestates.server.config.TestUserDetailService;
import com.codestates.server.tag.controller.TagController;
import com.codestates.server.tag.dto.TagResponseDto;
import com.codestates.server.tag.entity.Tag;
import com.codestates.server.tag.mapper.TagMapper;
import com.codestates.server.tag.service.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestUserDetailService.class})
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagMapper tagMapper;

    @Test
    void getTagsTest() throws Exception {
        //given
        int page = 1;
        int size = 10;
        long totalElements = 9;
        List<Tag> tags = new ArrayList<>();
        for (long i = 0; i < totalElements; i++) {
            tags.add(new Tag());
        }

        Page<Tag> pageTags = new PageImpl<>(
                tags, PageRequest.of(page - 1, size, Sort.by("tagId").descending()), 1
        );

        List<TagResponseDto> response = new ArrayList<>();
        for (long i = totalElements; i >= 1; i--) {
            TagResponseDto tagResponseDto = new TagResponseDto();
            tagResponseDto.setTagId(i);
            tagResponseDto.setCategory("??????" + i);
            tagResponseDto.setQuestionsCount(3);
            response.add(tagResponseDto);
        }

        given(tagService.findTags(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).willReturn(pageTags);
        given(tagMapper.tagsToTagResponseDtos(Mockito.anyList())).willReturn(response);

        //when
        String keyword = "";
        String tab = "new";

        ResultActions actions = mockMvc.perform(
                get("/tags?page={page}&size={size}&keyword={keyword}&tab={tab}",
                        page, size, keyword, tab)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageInfo.page").value(page))
                .andExpect(jsonPath("$.pageInfo.size").value(size))
                .andDo(document(
                        "get-tags",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("????????? ????????? (defaultValue = 1)"),
                                parameterWithName("size").description("???????????? ????????? ??? (defaultValue = 36)"),
                                parameterWithName("keyword").description("?????????"),
                                parameterWithName("tab").description("?????? ?????? (defaultValue = popular (?????????), ????????? = name, ????????? ???????????? ??? = new)")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                        fieldWithPath("data[*].tagId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[*].category").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[*].questionsCount").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? ???"),
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
}
