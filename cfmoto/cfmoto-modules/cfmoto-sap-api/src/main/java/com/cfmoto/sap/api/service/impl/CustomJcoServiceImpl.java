package com.cfmoto.sap.api.service.impl;

import cn.hutool.core.date.DateUtil;
import com.cfmoto.sap.api.config.CustomDestinationDataProvider;
import com.cfmoto.sap.api.config.JcoProviderConfig;
import com.cfmoto.sap.api.service.CustomJcoService;
import com.cfmoto.sap.api.utils.CustomBusinessException;
import com.github.pig.common.util.R;
import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class CustomJcoServiceImpl implements CustomJcoService {

    @Autowired
    private JcoProviderConfig jcoProviderConfig;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

    //构造方法之后执行
    @PostConstruct
    public void init(){

        /**
         * 初始化配置参数 连接池
         */
        Properties connectProperties = new Properties();
        //ERP服务器IP地址
        connectProperties.setProperty( DestinationDataProvider.JCO_ASHOST, jcoProviderConfig.getJcoAshost() );
        //实例编号
        connectProperties.setProperty( DestinationDataProvider.JCO_SYSNR,  jcoProviderConfig.getJcoSysnr() );
        //客户端
        connectProperties.setProperty( DestinationDataProvider.JCO_CLIENT, jcoProviderConfig.getJcoClient() );
        //用户名
        connectProperties.setProperty( DestinationDataProvider.JCO_USER,   jcoProviderConfig.getJcoUser() );
        //密码
        connectProperties.setProperty( DestinationDataProvider.JCO_PASSWD, jcoProviderConfig.getJcoPasswd() );
        // JCO_PEAK_LIMIT - 同时可创建的最大活动连接数，0表示无限制，默认为JCO_POOL_CAPACITY的值
        // 如果小于JCO_POOL_CAPACITY的值，则自动设置为该值，在没有设置JCO_POOL_CAPACITY的情况下为0
        connectProperties.setProperty( DestinationDataProvider.JCO_POOL_CAPACITY, jcoProviderConfig.getJcoPoolCapacity() );
        //同时可创建的最大活动连接数，0表示无限制，默认为JCO_POOL_CAPACITY的值
        connectProperties.setProperty( DestinationDataProvider.JCO_PEAK_LIMIT,   jcoProviderConfig.getJcoPeakLimit() );
        //语言
        connectProperties.setProperty( DestinationDataProvider.JCO_LANG,   jcoProviderConfig.getJcoLang() );
        //开启R/3需要该行配置
        connectProperties.setProperty( DestinationDataProvider.JCO_SAPROUTER,   jcoProviderConfig.getJcoSaprouter() );
        CustomDestinationDataProvider provider = new CustomDestinationDataProvider();
        Environment.registerDestinationDataProvider( provider );
        provider.addDestinationProperties( jcoProviderConfig.getJcoDestName(), connectProperties );

    }

    //测试连接是否连通
    @Override
    public R pingCalls(String destName ){
        JCoDestination dest;
        try{
            dest = JCoDestinationManager.getDestination( destName );
            dest.ping();
            return new R<>( R.SUCCESS, "success" );
        }catch( JCoException e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>( R.FAIL, ExceptionUtils.getFullStackTrace( e ) );
        }
    }

    /**
     * 传入功能名称和Map类型参数
     * @param functionName
     * @param paramMap
     * @return
     */
    @Override
    public R execute(String functionName, Map<String, Object> paramMap ) {
        Map resultMap = new HashMap();

        //传入参数 ---------------------------------------------------------------------------------------------------------------------------------------
        try {
            JCoDestination conn = JCoDestinationManager.getDestination( jcoProviderConfig.getJcoDestName() );
            JCoFunction fun = conn.getRepository().getFunction( functionName );
            if( fun==null ){
                return new R( R.FAIL,functionName+"不存在" );
            }

            JCoParameterList input = fun.getImportParameterList();
            if( paramMap != null ){
                for( Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> pairs = it.next();
                    if( pairs.getValue() instanceof List) {

                        setTableParamList( fun, pairs );

                    } else if( pairs.getValue() instanceof Map ) {

                        setImportParameterList( fun, pairs );

                    } else {
                        input.setValue( "" + pairs.getKey(), pairs.getValue() );
                    }
                }
            }

            JCoContext.begin( conn );
            try {
                //执行方法
                Date startDate = new Date();
                log.info( functionName+"执行开始时间："+ DateUtil.format( startDate,"yyyy-MM-dd HH:mm:ss.SSS" ) );
                fun.execute( conn );
                Date endDate = new Date();
                log.info( functionName+"执行结束时间："+ DateUtil.format( endDate,"yyyy-MM-dd HH:mm:ss.SSS" )
                        +"，总计运行："+ ( endDate.getTime()-startDate.getTime() )+"ms" );
            } finally {
                JCoContext.end( conn );
            }
            if( fun.getExportParameterList() != null ) {
                getExportParameterList( fun, resultMap );
            }

            if( fun.getTableParameterList() != null ) {
                getTableParameterList( fun, resultMap );
            }
        } catch( JCoException e ) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, ExceptionUtils.getFullStackTrace( e ) );
        } catch ( CustomBusinessException e) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, ExceptionUtils.getFullStackTrace( e ) );
        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, ExceptionUtils.getFullStackTrace( e ) );
        }
        return new R<Map>( resultMap );
    }


    //设置表格传入参数
    private void setTableParamList( JCoFunction fun, Map.Entry<String, Object> pairs ) throws CustomBusinessException{
        JCoTable tb = fun.getTableParameterList().getTable( "" + pairs.getKey() );
        List ls = (List) pairs.getValue();

        for( int i = 0; i < ls.size(); i++ ) {
            Map<String, String> m = (Map<String, String>) ls.get( i );
            tb.appendRow();
            for( JCoFieldIterator jft = tb.getFieldIterator(); jft.hasNextField(); ) {
                JCoField p = jft.nextField();
                if( "date".equalsIgnoreCase( p.getTypeAsString() ) ) {
                    if( m.containsKey( p.getName() ) ) {
                        if( !"".equals( m.get( p.getName() ) ) ) {
                            try {
                                p.setValue( simpleDateFormat.parse( m.get( p.getName() ) ) );
                            } catch (ParseException e) {
                                throw new CustomBusinessException( e );
                            }
                        } else {
                            p.setValue( "" );
                        }
                    }
                } else {
                    if( m.containsKey( p.getName() ) ) {
                        if( m.get( p.getName() ) == null ) {
                            throw new CustomBusinessException( "参数" + p.getName() + "为null" );
                        }
                        p.setValue( m.get( p.getName() ) );
                    }
                }
            }
        }
    }

    //设置列表传入参数
    private void setImportParameterList(JCoFunction fun, Map.Entry<String, Object> pairs) throws CustomBusinessException{
        Map<String, String> pairsMap = (Map<String, String>) pairs.getValue();
        JCoStructure jcos = fun.getImportParameterList().getStructure( "" + pairs.getKey() );

        for( JCoFieldIterator jft = jcos.getFieldIterator(); jft.hasNextField(); ) {
            JCoField jf = jft.nextField();

            if( "date".equalsIgnoreCase( jf.getTypeAsString() ) ) {
                if( pairsMap.containsKey( jf.getName() ) ) {
                    if( !"".equals( pairsMap.get( jf.getName() ) ) ) {
                        try {
                            jf.setValue( simpleDateFormat.parse( pairsMap.get( jf.getName() ) ) );
                        } catch (ParseException e) {
                            throw new CustomBusinessException( e );
                        }
                    } else {
                        jf.setValue( "" );
                    }
                } else {
                    throw new CustomBusinessException( "参数错误，没有准备参数【" + jf.getName() + "】" );
                }
            } else {

                if( pairsMap.containsKey( jf.getName() ) ) {
                    if( pairsMap.get( jf.getName() ) == null ) {
                        throw new CustomBusinessException( "参数" + jf.getName() + "为null" );
                    }
                    jf.setValue( pairsMap.get( jf.getName() ) );
                }
            }
        }
    }

    /**
     * 获取输出参数列表
     * @param fun
     * @param resultMap
     */
    private static void getExportParameterList( JCoFunction fun, Map resultMap ){
        for( Iterator<JCoField> it = fun.getExportParameterList().iterator(); it.hasNext(); ) {
            JCoField o = it.next();
            if( o.isTable() ) {
                JCoTable tb = (JCoTable) o;
                List resultList = new ArrayList();
                for( int i = 0; i < tb.getNumRows(); i++ ) {
                    Map retMap = new HashMap();
                    tb.setRow( i );
                    retMap = new HashMap();
                    for( JCoRecordFieldIterator itA = tb.getRecordFieldIterator(); itA.hasNextField(); ) {
                        JCoField field = itA.nextField();
                        retMap.put( field.getName(), tb.getString( field.getName() ) );
                    }
                    resultList.add( retMap );
                }
                resultMap.put( "" + o.getName(), resultList );
            } else if( o.isStructure() ) {
                JCoStructure st = o.getStructure();
                Map resutStructureMap = new HashMap();
                for( JCoFieldIterator jft = st.getFieldIterator(); jft.hasNextField(); ) {
                    JCoField jf = jft.nextField();
                    resutStructureMap.put( jf.getName(), jf.getValue() );
                }
                resultMap.put( "" + o.getName(), resutStructureMap );
            } else {
                resultMap.put( "" + o.getName(), o.getValue() );
            }
        }
    }

    private static void getTableParameterList( JCoFunction fun, Map resultMap ){
        for( Iterator<JCoField> it = fun.getTableParameterList().iterator(); it.hasNext(); ) {
            JCoField o = it.next();
            if( o.isTable() ) {
                JCoTable tb = o.getTable();
                List resultList = new ArrayList();
                for( int i = 0; i < tb.getNumRows(); i++ ) {
                    Map retMap = new HashMap();
                    tb.setRow( i );
                    retMap = new HashMap();
                    for( JCoRecordFieldIterator itA = tb.getRecordFieldIterator(); itA.hasNextField(); ) {
                        JCoField field = itA.nextField();
                        retMap.put( field.getName(), tb.getString( field.getName() ) );
                    }
                    resultList.add( retMap );
                }
                resultMap.put( "" + o.getName(), resultList );
            } else if( o.isStructure() ) {
                JCoStructure st = o.getStructure();
                Map resutStructureMap = new HashMap();
                for( JCoFieldIterator jft = st.getFieldIterator(); jft.hasNextField(); ) {
                    JCoField jf = jft.nextField();
                    resutStructureMap.put( jf.getName(), jf.getValue() );
                }
                resultMap.put( "" + o.getName(), resutStructureMap );
            } else {
                resultMap.put( "" + o.getName(), o.getValue() );
            }
        }
    }


}
