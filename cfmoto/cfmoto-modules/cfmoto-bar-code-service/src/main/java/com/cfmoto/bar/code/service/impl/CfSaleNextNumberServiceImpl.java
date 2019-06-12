package com.cfmoto.bar.code.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfSaleNextNumberMapper;
import com.cfmoto.bar.code.model.entity.CfSaleNextNumber;
import com.cfmoto.bar.code.service.ICfSaleNextNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 获取销售订单下一编号 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-12
 */
@Service
public class CfSaleNextNumberServiceImpl extends ServiceImpl<CfSaleNextNumberMapper, CfSaleNextNumber> implements ICfSaleNextNumberService {

    @Autowired
    private CfSaleNextNumberMapper cfSaleNextNumberMapper;

    /**
     * 获取销售订单箱号
     * @param userId
     * @param saleOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public String generateSaleCaseNoNextNumber(Integer userId, String saleOrder) throws Exception {

        return formatNextValues( userId, saleOrder );

    }

    public CfSaleNextNumber createCfSaleNextNumber( Integer userId, String saleOrder ){
        CfSaleNextNumber cfSaleNextNumber = new CfSaleNextNumber();
        cfSaleNextNumber.setSaleOrder( saleOrder );
        cfSaleNextNumber.setDescription( saleOrder );
        cfSaleNextNumber.setPrefix( saleOrder );
        cfSaleNextNumber.setSequenceLength( new BigDecimal( 4 ) );
        cfSaleNextNumber.setCurrentSequence( new BigDecimal( 0 ) );
        cfSaleNextNumber.setMinSequence( new BigDecimal( 1 ) );
        cfSaleNextNumber.setIncr( new BigDecimal( 1 ) );
        cfSaleNextNumber.setReset( "N" );
        cfSaleNextNumber.setObjectSetBasicAttribute( userId, new Date() );
        return cfSaleNextNumber;
    }

    /**
     * 生成并格式化生成的编号
     * @param userId
     * @param saleOrder
     * @return
     * @throws Exception
     */
    public String formatNextValues( Integer userId,String saleOrder ) throws Exception {

        CfSaleNextNumber saleNextNumber = new CfSaleNextNumber();
        saleNextNumber.setSaleOrder( saleOrder );
        CfSaleNextNumber cfSaleNextNumber = cfSaleNextNumberMapper.selectBySaleOrderForUpdate( saleOrder );
        if( cfSaleNextNumber==null ){
            cfSaleNextNumber = createCfSaleNextNumber( userId, saleOrder );
            cfSaleNextNumberMapper.insert( cfSaleNextNumber );
        }
        BigDecimal currentSequence = cfSaleNextNumber.getCurrentSequence(); //当前值
        if( currentSequence==null ){
            currentSequence = BigDecimal.ZERO;
        }
        String prefix = cfSaleNextNumber.getPrefix(); //前缀
        BigDecimal sequenceLength = cfSaleNextNumber.getSequenceLength(); //编号长度
        BigDecimal incrDecimal = cfSaleNextNumber.getIncr(); //编号步长
        if( incrDecimal==null ){
            incrDecimal = new BigDecimal( 1 );
        }
        BigDecimal nextSequence = currentSequence.add( incrDecimal ); //下一编号
        boolean resetFlag = shouldResetOccur( cfSaleNextNumber.getReset(),new Date(),cfSaleNextNumber.getLastUpdateDate() );
        if( resetFlag ){ //编号重置
            nextSequence = cfSaleNextNumber.getMinSequence();
        }
        //拼接长度
        String nextSequenceStr = nextSequence.toString();
        if( nextSequenceStr.length() < sequenceLength.intValue() ){
            while ( nextSequenceStr.toString().length() < sequenceLength.intValue() ){
                nextSequenceStr = "0"+nextSequenceStr;
            }
        }
        String formatStr = null;
        if( prefix!=null && prefix.contains( "%" ) ){
            formatStr = prefix.substring( prefix.indexOf( "%" )+1, prefix.lastIndexOf( "%" ) );
        }
        String formatVal = null;
        if( formatStr!=null ){
            formatVal = DateUtil.format( new Date(), formatStr );
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( formatVal==null?(prefix==null?"":prefix):prefix.replace( "%"+formatStr+"%", formatVal ) )
                .append( nextSequenceStr ).append( cfSaleNextNumber.getSuffix()==null?"":cfSaleNextNumber.getSuffix() );

        Date date = new Date();
        CfSaleNextNumber updateCfSaleNextNumber = new CfSaleNextNumber();
        updateCfSaleNextNumber.setSaleNextNumberId( cfSaleNextNumber.getSaleNextNumberId() );
        updateCfSaleNextNumber.setCurrentSequence( nextSequence );
        updateCfSaleNextNumber.setLastUpdatedBy( userId );
        updateCfSaleNextNumber.setLastUpdateDate( date );
        cfSaleNextNumberMapper.updateById( updateCfSaleNextNumber ); //更新编号
        return stringBuilder.toString();

    }


    public static Calendar calendar(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }

    //判断同年同周
    public boolean isSameWeek( Date date1, Date date2 ){
        if (date1 != null && date2 != null) {
            return isSameWeek( calendar ( date1.getTime() ),calendar( date2.getTime() ) );
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
    //判断同年同周
    public boolean isSameWeek( Calendar calA, Calendar calB ){
        if (calA != null && calB != null) {
            return calA.get( 1 ) == calB.get( 1 ) && calA.get( 2 ) == calB.get( 2 ) && calA.get( 3 ) == calB.get(3);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    //判断同年同月
    public boolean isSameMonth( Date date1, Date date2 ){
        if (date1 != null && date2 != null) {
            return isSameMonth( calendar ( date1.getTime() ),calendar( date2.getTime() ) );
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
    //判断同年同月
    public boolean isSameMonth( Calendar calA, Calendar calB ){
        if (calA != null && calB != null) {
            return calA.get( 1 ) == calB.get( 1 ) && calA.get( 2 ) == calB.get( 2 );
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    //判断同年
    public boolean isSameYear( Date date1, Date date2 ){
        if (date1 != null && date2 != null) {
            return isSameYear( calendar ( date1.getTime() ),calendar( date2.getTime() ) );
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
    //判断同年
    public boolean isSameYear( Calendar calA, Calendar calB ){
        if (calA != null && calB != null) {
            return calA.get( 1 ) == calB.get( 1 );
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * 判断是否需要重置序号
     * N: 从不
     * A: 总是
     * D: 每天
     * W: 每周
     * M: 每月
     * Y: 每年
     * E: 每周 - 星期日
     * F: 每周 - 星期一
     * G: 每周 - 星期二
     * H: 每周 - 星期三
     * I: 每周 - 星期四
     * J; 每周 - 星期五
     * K: 每周 - 星期六
     * @param reset
     * @param lastUpdateDT
     * @param currentDT
     * @return
     */
    public boolean shouldResetOccur( String reset, Date lastUpdateDT, Date currentDT ) {

        if (reset.equals("A"))
            return true;
        if ((reset.equals("D")) && !DateUtil.isSameDay(lastUpdateDT, currentDT) )
            return true;
        if ((reset.equals("W")) && !isSameWeek(lastUpdateDT, currentDT) )
            return true;
        if ((reset.equals("M")) && !isSameMonth(lastUpdateDT, currentDT) )
            return true;
        if ((reset.equals("Y")) && !isSameYear(lastUpdateDT, currentDT) ) {
            return true;
        }

        if ((reset.equals("E")) && (dayResetNeeded(lastUpdateDT, currentDT, "E"))) {
            return true;
        }
        if ((reset.equals("F")) && (dayResetNeeded(lastUpdateDT, currentDT, "F"))) {
            return true;
        }
        if ((reset.equals("G")) && (dayResetNeeded(lastUpdateDT, currentDT, "G"))) {
            return true;
        }
        if ((reset.equals("H")) && (dayResetNeeded(lastUpdateDT, currentDT, "H"))) {
            return true;
        }
        if ((reset.equals("I")) && (dayResetNeeded(lastUpdateDT, currentDT, "I"))) {
            return true;
        }
        if ((reset.equals("J")) && (dayResetNeeded(lastUpdateDT, currentDT, "J"))) {
            return true;
        }
        if ((reset.equals("K")) && (dayResetNeeded(lastUpdateDT, currentDT, "K"))) {
            return true;
        }

        return false;
    }

    /**
     * 是否需要按固定周天重置
     * @param lastUpdateDT
     * @param currentDT
     * @param resetDay
     * @return
     */
    public boolean dayResetNeeded(Date lastUpdateDT, Date currentDT, String resetDay ) {
        Calendar currentDateTime = Calendar.getInstance();
        currentDateTime.setTime(currentDT);
        currentDateTime.set(currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DATE), 23, 59, 59);

        Calendar lastUPdateDateTime = Calendar.getInstance();
        lastUPdateDateTime.setTime(lastUpdateDT);

        long diffDays = (lastUPdateDateTime.getTimeInMillis() - currentDateTime.getTimeInMillis()) / (1000 * 3600 * 24);
        int daysBetween = (int) diffDays;

        String lastUpdateDate = new StringBuilder().append(lastUPdateDateTime.get(Calendar.YEAR)).append(lastUPdateDateTime.get(Calendar.MONTH)).append(lastUPdateDateTime.get(Calendar.DATE)).toString();
        String currentDate = new StringBuilder().append(currentDateTime.get(Calendar.YEAR)).append(currentDateTime.get(Calendar.MONTH)).append(currentDateTime.get(Calendar.DATE)).toString();
        if (lastUpdateDate.equals(currentDate)) {
            return false;
        }

        if (daysBetween < -6) {
            return true;
        }

        Calendar nextDay = Calendar.getInstance();
        nextDay.setTime(lastUpdateDT);

        int dayDiffCount = Math.abs(daysBetween);
        int resetDow = getDayNumberFromResetDay(resetDay);
        for (int i = 0; i < dayDiffCount; i++) {
            nextDay.add(Calendar.DATE, 1);
            int dayNumber = nextDay.get(Calendar.DAY_OF_WEEK);
            if (dayNumber == resetDow) {
                return true;
            }
        }

        return false;
    }


    public int getDayNumberFromResetDay(String reset) {
        if (reset.equals("E")) {
            return 0;
        }
        if (reset.equals("F")) {
            return 1;
        }
        if (reset.equals("G")) {
            return 2;
        }
        if (reset.equals("H")) {
            return 3;
        }
        if (reset.equals("I")) {
            return 4;
        }
        if (reset.equals("J")) {
            return 5;
        }
        if (reset.equals("K")) {
            return 6;
        }
        return -1;
    }

}
