package fr.dawan.cc_spring.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import fr.dawan.cc_spring.dto.PageResponse;

import org.modelmapper.config.Configuration;

@Component 
public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public <S, D> List<D> mapList(List<S> sources, Class<D> destinationType) {
        return sources.stream().map(s -> map(s, destinationType)).toList();
    }

    public <S, D> PageResponse<D> mapPage(Page<S> page, Class<D> destinationType) {
        return PageResponse.from(page.map(s -> map(s, destinationType)));
    }
}
