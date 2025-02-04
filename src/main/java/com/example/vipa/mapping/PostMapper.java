package com.example.vipa.mapping;

import com.example.vipa.dto.PostDetailsDto;
import com.example.vipa.dto.PostImageDto;
import com.example.vipa.dto.PostPreviewDto;
import com.example.vipa.model.Post;
import com.example.vipa.model.PostImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PostMapper {

    private final ModelMapper modelMapper;

    public PostMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        Converter<List<PostImage>, PostImageDto> imageListToImageDtoConverter =
                src -> modelMapper.map(src.getSource().get(0), PostImageDto.class);
        Condition<List<PostImage>, PostImageDto> imageListToImageDtoCondition =
                src -> src.getSource().isEmpty();
        Converter<List<PostImage>, List<PostImageDto>> imageListToImageDtoListConverter =
                src -> src.getSource().stream()
                        .map(image -> modelMapper.map(image, PostImageDto.class))
                        .toList();
        Converter<List<String>, List<PostImage>> urlListToImageListConverter =
                src -> src.getSource().stream()
                        .map(url -> new PostImage().setUrl(url))
                        .toList();
        modelMapper.createTypeMap(Post.class, PostPreviewDto.class)
                .addMappings(mapper -> mapper.using(imageListToImageDtoConverter)
                        .map(Post::getImages, PostPreviewDto::setCoverImage))
                .addMappings(mapper -> mapper.when(imageListToImageDtoCondition)
                        .skip(Post::getImages, PostPreviewDto::setCoverImage));
        modelMapper.createTypeMap(Post.class, PostDetailsDto.class)
                .addMappings(mapper -> mapper.using(imageListToImageDtoListConverter)
                        .map(Post::getImages, PostDetailsDto::setImages));
        modelMapper.createTypeMap(PostDetailsDto.class, Post.class)
                .addMappings(mapper -> mapper.using(urlListToImageListConverter)
                        .map(PostDetailsDto::getImages, Post::setImages));
    }

    public Post convertToPost(PostDetailsDto dto) {
        log.info("inside convertToPost()");
        return modelMapper.map(dto, Post.class);
    }

    public PostPreviewDto convertToPostPreviewDto(Post post) {
        log.info("inside convertToPostPreviewDto()");
        return modelMapper.map(post, PostPreviewDto.class);
    }

    public PostDetailsDto convertToPostDetailsDto(Post post) {
        log.info("inside convertToPostDetailsDto()");
        return modelMapper.map(post, PostDetailsDto.class);
    }
}
