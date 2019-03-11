
package com.huawei.unistar.webcfg.model.commerce.dao.impl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtils
{
    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static LinkedBuffer buffer = LinkedBuffer
            .allocate( LinkedBuffer.DEFAULT_BUFFER_SIZE );
    /**
     * 缓存Schema
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * 序列化方法，把指定对象序列化成字节数组
     * 
     * @param obj
     * @param <T>
     * @return
     */
    @SuppressWarnings( "unchecked" )
    public static <T> byte[] serialize( T obj )
    {
        Class<T> clazz = (Class<T>)obj.getClass();
        Schema<T> schema = getSchema( clazz );
        byte[] data;
        try
        {
            data = ProtostuffIOUtil.toByteArray( obj, schema, buffer );
        } finally
        {
            buffer.clear();
        }

        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     * 
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize( byte[] data, Class<T> clazz )
    {
        Schema<T> schema = getSchema( clazz );
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom( data, obj, schema );
        return obj;
    }

    @SuppressWarnings( "unchecked" )
    private static <T> Schema<T> getSchema( Class<T> clazz )
    {
        Schema<T> schema = (Schema<T>)schemaCache.get( clazz );
        if ( schema == null )
        {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema( clazz );
            if ( schema != null )
            {
                schemaCache.put( clazz, schema );
            }
        }

        return schema;
    }

    public static <T> byte[] serializeList( List<T> objList )
    {
        if ( objList == null || objList.isEmpty() )
        {
            throw new RuntimeException( "序列化对象列表(" + objList + ")参数异常!" );
        }
        @SuppressWarnings( "unchecked" )
        Schema<T> schema = (Schema<T>)RuntimeSchema.getSchema( objList.get( 0 )
                .getClass() );
        LinkedBuffer buffer = LinkedBuffer.allocate( 1024 * 1024 );
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try
        {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo( bos, objList, schema, buffer );
            protostuff = bos.toByteArray();
        } catch ( Exception e )
        {
            throw new RuntimeException( "序列化对象列表(" + objList + ")发生异常!", e );
        } finally
        {
            buffer.clear();
            try
            {
                if ( bos != null )
                {
                    bos.close();
                }
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        return protostuff;
    }

    public static <T> List<T> deserializeList( byte[] paramArrayOfByte,
            Class<T> targetClass )
    {
        if ( paramArrayOfByte == null || paramArrayOfByte.length == 0 )
        {
            throw new RuntimeException( "反序列化对象发生异常,byte序列为空!" );
        }

        Schema<T> schema = RuntimeSchema.getSchema( targetClass );
        List<T> result = null;
        try
        {
            result = ProtostuffIOUtil.parseListFrom( new ByteArrayInputStream(
                    paramArrayOfByte ), schema );
        } catch ( IOException e )
        {
            throw new RuntimeException( "反序列化对象列表发生异常!", e );
        }
        return result;
    }
}
