package com.codestates.server.comment.controller;

import com.codestates.server.answer.repository.AnswerRepository;
import com.codestates.server.comment.dto.CommentDto;
import com.codestates.server.comment.entity.Comment;
import com.codestates.server.comment.mapper.CommentMapper;
import com.codestates.server.comment.service.CommentService;
import com.codestates.server.dto.SingleResponseDto;
import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper mapper;
    private final AnswerRepository answerRepository;

    @PostMapping("/questions/{question-id}/comments")
    public ResponseEntity postQuestionComment(@PathVariable("question-id") Long questionId,
                                       @Valid @RequestBody CommentDto.Post requestBody) {

        Comment comment = mapper.CommentPostDtoToComment(requestBody);
        Comment response = commentService.postQuestionComment(questionId, comment);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.CommentToCommentResponseDto(response)), HttpStatus.CREATED);
    }

    @PostMapping("/answers/{answers-id}/comments")
    public ResponseEntity postAnswerComment( @PathVariable("answer-id") Long answerId,
                                       @Valid @RequestBody CommentDto.Post requestBody) {

        Comment comment = mapper.CommentPostDtoToComment(requestBody);
        Comment response = commentService.postAnswerComment(answerId, comment);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.CommentToCommentResponseDto(response)), HttpStatus.CREATED);

    }
    @PatchMapping("/comments/{comment-id}}")
    public ResponseEntity patchComment(@RequestParam("comment-id") Long commentId,
                                       @RequestBody CommentDto.Patch requestBody,
                                       HttpServletRequest request) {

        Comment comment = mapper.CommentPatchDtoToComment(requestBody);
        Comment response = commentService.updateComment(commentId, comment);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.CommentToCommentResponseDto(response)), HttpStatus.OK);
    }
    @DeleteMapping("/comments/{comment-id}")
    public ResponseEntity deleteComment(@RequestParam("comment-id") Long commentId,
                                        HttpServletRequest request) {

        commentService.deleteComment(commentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

