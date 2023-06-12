package com.myblog.blogapp.service.impl;

import com.myblog.blogapp.entities.Post;
import com.myblog.blogapp.exception.ResourceNotFoundException;
import com.myblog.blogapp.payload.PostDto;
import com.myblog.blogapp.payload.PostResponse;
import com.myblog.blogapp.repository.PostRepository;
import com.myblog.blogapp.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepo;
    
    private ModelMapper mapper;//mapper library

    public PostServiceImpl(PostRepository postRepo,ModelMapper mapper) {
        
        this.postRepo = postRepo;
        this.mapper=mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        Post post=mapToEntity(postDto);// this using for ex getmappin,post etc...dto to entity

        Post postEntity = postRepo.save(post);

       PostDto dto= mapToDto(postEntity);//entity object maptodto convert dto
        return dto;
    }

    @Override
    public PostResponse getAllPosts(int pageNo,int pageSize,String sortBy,String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize, sort);
         Page<Post> posts = postRepo.findAll(pageable);//entity object is conveting dto object
        List<Post> content = posts.getContent();

        List<PostDto> contents = content.stream().map(post -> mapToDto(post)).collect(Collectors.toList());//lamdsexpre java8 fea no using forloop

        PostResponse postResponse=new PostResponse();
        postResponse.setContent(contents);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setLast(posts.isLast());
        return postResponse;

    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("post", "id", id)//lamds exp apply

        );

        PostDto postDto = mapToDto(post);
        return postDto;
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("post", "id", id)
        );
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post newPost = postRepo.save(post);
        return mapToDto(newPost);

    }

    @Override
    public void deletePost(long id) {
        postRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("post","id",id)

        );
        postRepo.deleteById(id);

    }

    public Post mapToEntity(PostDto postDto){

        Post post = mapper.map(postDto, Post.class);


//            Post post=new Post();
//            // converting dto to entity
//            post.setTitle(postDto.getTitle());
//            post.setDescription(postDto.getDescription());
//            post.setContent(postDto.getContent());
            
            return post;

        }
        public PostDto mapToDto(Post post){
            //converting entity to dto

            PostDto dto = mapper.map(post, PostDto.class);

//            PostDto dto=new PostDto();
//            dto.setId(post.getId());
//            dto.setTitle(post.getTitle());
//            dto.setDescription(post.getDescription());
//            dto.setContent(post.getContent());
            
            
            return dto;

        }

    }
