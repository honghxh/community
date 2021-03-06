package com.munity.serviceImpl;

import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.Followees;
import com.munity.pojo.model.Post;
import com.munity.service.DiscussPostService;
import com.munity.service.FollowService;
import com.munity.service.UserService;
import com.munity.util.CommunityConstant;
import com.munity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author forrest
 */
@Service
public class FollowServicImpl implements CommunityConstant, FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
    @Autowired
    DiscussPostService discussPostService;

    @Override
    public void follow(int userId, int entityType, int entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    // ??????????????????????????????
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // ??????????????????????????????
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // ??????????????????????????????????????????
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // ???????????????????????????
    @Override
    public List<Followees> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Followees> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            User user = userService.findUserById(targetId);
            Followees followees = new Followees(user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            followees.setFollowTime(new Date(score.longValue()));
            list.add(followees);
        }

        return list;
    }

    @Override
    public List<Post> findFolloweesPost(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_POST);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Post> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            //???????????????????????????????????????????????? followservice ??? discusspostservice ??????????????????
            Post post = discussPostService.findDiscussPostById(targetId);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            post.setFollowTime(new Date(score.longValue()));
            list.add(post);
        }

        return list;
    }

    // ????????????????????????
    @Override
    public List<Followees> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Followees> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            User user = userService.findUserById(targetId);
            Followees followees = new Followees(user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            followees.setFollowTime(new Date(score.longValue()));
            list.add(followees);
        }

        return list;
    }

}
