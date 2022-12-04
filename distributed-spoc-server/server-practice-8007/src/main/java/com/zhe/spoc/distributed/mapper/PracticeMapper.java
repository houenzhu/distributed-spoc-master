package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName PracticeMapper
 * @Description TODO
 * @date 2022/10/23 16:34
 * @Version 1.0
 */

@Mapper
public interface PracticeMapper extends BaseMapper<PracticeEntity> {
    Boolean insertPractice(PracticeEntity practiceEntity);

    Boolean removePractice(@Param("praId") Long praId);

    List<PracticeEntity> practiceList(@Param("teaId") Integer teaId, @Param("begin") Integer begin, @Param("count") Integer count);

    List<PracticeEntity> practiceListByName(@Param("teaId") Integer teaId, @Param("praName") String praName,
                                            @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    /**
     * 模糊查询全部
     * @param praName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PracticeEntity> AllpracticeListByName(@Param("praName") String praName,
                                            @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    PracticeEntity practiceListById(@Param("teaId") Integer teaId, @Param("praId") Long praId);

    /**
     * id查询全部
     * @param praId
     * @return
     */
    PracticeEntity practiceById(@Param("praId") Long praId);

    Boolean insertStuPractice(StuPracticeEntity stuPracticeEntity);

    Integer checkPractice(StuPracticeEntity stuPracticeEntity);

    List<PracticeEntity> SelectAllPractice();

    // 教师查看全部线下实践信息
    List<PracticeEntity> SelectAllPracticeInPage(@Param("begin") Integer begin, @Param("count") Integer count);

    // 查看教师本人发布的实践
    Integer MyPracticeCount(@Param("teaId") Integer teaId);

    /**
     * 实践数量
     * @return
     */
    int PracticeNum();

    /**
     * 更新开始状态
     * @param practiceEntity
     */
    boolean updateStatus(PracticeEntity practiceEntity);

    /**
     * 模糊查询我发布的实践的数量
     * @param praName
     * @return
     */
    int myPracticeByNameNum(@Param("praName") String praName, @Param("teaId") Integer teaId);

    /**
     * id查询我发布的实践的数量
     * @param praId
     * @return
     */
    int myPracticeByIdNum(@Param("praId") Long praId, @Param("teaId") Integer teaId);


    /**
     * 模糊查询发布的实践的数量
     * @param praName
     * @return
     */
    int PracticeByNameNum(@Param("praName") String praName);

    /**
     * id查询发布的实践的数量
     * @param praId
     * @return
     */
    int PracticeByIdNum(@Param("praId") Long praId);

    Boolean updatePractice(PracticeEntity practiceEntity);
}
