package com.min.simplesns.controller;

import com.min.simplesns.controller.request.PostCommentRequest;
import com.min.simplesns.controller.request.PostCreateRequest;
import com.min.simplesns.controller.request.PostModifyRequest;
import com.min.simplesns.controller.response.CommentResponse;
import com.min.simplesns.controller.response.PostResponse;
import com.min.simplesns.controller.response.Response;
import com.min.simplesns.model.Post;
import com.min.simplesns.model.User;
import com.min.simplesns.service.PostService;
import com.min.simplesns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication){
        postService.create(request.getTitle(), request.getBody(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication){
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).get();
        return Response.success(
                PostResponse.fromPost(
                        postService.modify(request.getTitle(), request.getBody(), user.getId(), postId)));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication){
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).get();
        postService.delete(user.getId(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication){
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication){
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).get();
        return Response.success(postService.my(user.getId(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication){
        postService.like(postId, authentication.getName());

        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Integer> likeCount(@PathVariable Integer postId, Authentication authentication){

        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication){
        postService.comment(postId, authentication.getName(), request.getComment());

        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comment(@PathVariable Integer postId, Pageable pageable, Authentication authentication){
        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }
}
