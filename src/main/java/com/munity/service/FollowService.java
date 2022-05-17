package com.munity.service;

import com.munity.pojo.model.Followees;

import java.util.List;

/**
 * @author forrest
 */
public interface FollowService {
    public void follow(int userId, int entityType, int entityId);
    public void unfollow(int userId, int entityType, int entityId) ;
    public long findFolloweeCount(int userId, int entityType);
    public long findFollowerCount(int entityType, int entityId);
    public boolean hasFollowed(int userId, int entityType, int entityId);
    public List<Followees> findFollowees(int userId, int offset, int limit);
    public List<Followees> findFollowers(int userId, int offset, int limit);

}
