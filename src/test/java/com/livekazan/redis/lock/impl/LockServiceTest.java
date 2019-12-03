package com.livekazan.redis.lock.impl;

import com.livekazan.redis.lock.IJLock;
import com.livekazan.redis.lock.exception.LockingException;
import com.livekazan.redis.lock.exception.UnlockingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfiguration.class)
public class LockServiceTest {

    @Autowired
    private LockService lockService;

    @Test
    public void acquireTest() throws LockingException, UnlockingException {
        IJLock lock = lockService.acquire("prefix:", "key1", 1000);
        assertNotNull(lock);
        lockService.release(lock);
    }

    @Test(expected = LockingException.class)
    public void acquireWithoutRelease() throws LockingException {
        IJLock lock = lockService.acquire("prefix:", "key2", 1000);
        assertNotNull(lock);
        IJLock lockFail = lockService.acquire("prefix:", "key2", 1000);
    }

    @Test
    public void acquireAndAcquireAgainByTimeOut() throws LockingException, InterruptedException {
        IJLock lock = lockService.acquire("prefix:", "key3", 5);
        assertNotNull(lock);
        Thread.sleep(50);
        //lock is expired so we can acquire it again
        IJLock lockAgain = lockService.acquire("prefix:", "key3", 1000);
        assertNotNull(lockAgain);
    }

    @Test
    public void acquireAndReleaseByTimeOut() throws LockingException, InterruptedException, UnlockingException {
        IJLock lock = lockService.acquire("prefix:", "key4", 5);
        assertNotNull(lock);
        Thread.sleep(10);
        lockService.release(lock);
        IJLock lockAgain = lockService.acquire("prefix:", "key4", 1000);
        assertNotNull(lockAgain);
    }

    @Test(expected = LockingException.class)
    @Repeat(10)
    public void acquireAtSameTime() throws LockingException, InterruptedException {
        IJLock lock = lockService.acquire("prefix:", "key5", 10);
        IJLock lockAgain = lockService.acquire("prefix:", "key5", 10);
    }

    @Test
    public void acquireAtSameTimeDifferentKey() throws LockingException, InterruptedException {
        IJLock lock = lockService.acquire("prefix:", "key6", 10);
        IJLock lockAnother = lockService.acquire("prefix:", "key7", 10);
    }
}