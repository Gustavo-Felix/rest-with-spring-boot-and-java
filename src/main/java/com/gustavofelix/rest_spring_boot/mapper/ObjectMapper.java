package com.gustavofelix.rest_spring_boot.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {

    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <O, D> D parseObject(O origin, Class<D> destination) { // Entity to DTO and DTO to Entity
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) { // Entity to DTO and DTO to Entity in list
        List<D> destinationObjects = new ArrayList<>();
        for (Object o : origin) {
            destinationObjects.add(mapper.map(o, destination));
        }

        return destinationObjects;
    }

}
