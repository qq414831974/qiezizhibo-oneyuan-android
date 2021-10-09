package com.qiezitv.common.http.factory;

import java.io.EOFException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmptyJsonLenientConverterFactory extends Converter.Factory {

    //修饰模式要求我们实现同样的接口，并且进行一定程度的委托，我们这边明确就是对 GsonConverterFactory 的功能进行扩充，所以我们的委托类型就直接声明为它
    private final GsonConverterFactory mGsonConverterFactory;

    public EmptyJsonLenientConverterFactory(GsonConverterFactory gsonConverterFactory) {
        mGsonConverterFactory = gsonConverterFactory;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        //request body 我们无需特殊处理，直接返回 GsonConverterFactory 创建的 converter
        return mGsonConverterFactory.requestBodyConverter(type,
                parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        //我们返回的 converter 可能会被多次使用，所以不要在匿名 converter 实例中创建委托 converter，而是只在外面创建一次
        final Converter<ResponseBody, ?> delegateConverter = mGsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        return value -> {
            try {
                //尝试把请求转发给 GsonConverterFactory 创建的 converter
                return delegateConverter.convert(value);
            } catch (EOFException e) {
                // just return null
                // 如果抛出了 EOFException，则说明遇到了空 JSON 字符串，那我们直接返回 null
                return null;
            }
        };
    }
}