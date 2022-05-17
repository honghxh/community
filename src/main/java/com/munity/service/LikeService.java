package com.munity.service;

public interface LikeService {
    public void like(int userId, int entityType, int entityId,int entityUserId);

    public long findEntityLikeCount(int entityType, int entityId);

    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId);

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId);
}
