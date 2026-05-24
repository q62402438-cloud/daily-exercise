const API_BASE_URL = 'http://192.168.88.101:8082/api';

async function request(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    const mergedOptions = { ...defaultOptions, ...options };
    
    if (mergedOptions.body && typeof mergedOptions.body === 'object') {
        mergedOptions.body = JSON.stringify(mergedOptions.body);
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, mergedOptions);
        
        if (!response.ok) {
            console.error(`HTTP错误: ${response.status} ${response.statusText}`);
            return { code: response.status, msg: `HTTP错误: ${response.status} ${response.statusText}` };
        }
        
        const text = await response.text();
        if (!text) {
            return { code: 200, msg: '操作成功' };
        }
        
        try {
            const data = JSON.parse(text);
            return data;
        } catch (jsonError) {
            console.warn('响应不是JSON格式:', text);
            return { code: 200, msg: '操作成功', data: text };
        }
    } catch (error) {
        console.error('API请求错误:', error);
        console.error('请求地址:', `${API_BASE_URL}${url}`);
        throw new Error('无法连接到服务器，请检查：\n1. 服务器是否已启动\n2. 服务器端口是否为 8082\n3. 网络连接是否正常');
    }
}

async function login(userName, userPassword, userType = 1) {
    return request('/auth/login', {
        method: 'POST',
        body: {
            userName: userName,
            userPassword: userPassword,
            userType: userType
        }
    });
}

async function register(userData) {
    return request('/auth/register', {
        method: 'POST',
        body: {
            userName: userData.userName,
            phoneNumber: userData.phoneNumber,
            userPassword: userData.userPassword
        }
    });
}

async function getUserInfo(userID) {
    return request('/user/getUserInfo', {
        method: 'POST',
        body: {
            userID: userID
        }
    });
}

async function updateUserInfo(userData) {
    return request('/user/update', {
        method: 'POST',
        body: {
            userID: userData.userID,
            userName: userData.userName,
            phoneNumber: userData.phoneNumber,
            userMailbox: userData.userMailbox,
            gender: userData.gender,
            birthday: userData.birthday,
            age: userData.age,
            weight: userData.weight,
            userPassword: userData.userPassword
        }
    });
}

async function checkUserNameExists(userName, excludeUserId) {
    return request('/user/checkUserName', {
        method: 'POST',
        body: {
            userName: userName,
            excludeUserId: excludeUserId
        }
    });
}

async function verifyPhone(phoneNumber) {
    return request('/auth/verifyPhone', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber
        }
    });
}

async function sendCode(phoneNumber) {
    return request('/auth/sendCode', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber
        }
    });
}

async function verifyCode(phoneNumber, code) {
    return request('/auth/verifyCode', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber,
            code: code
        }
    });
}

async function resetPassword(phoneNumber, verifyCode, userPassword) {
    return request('/auth/resetPassword', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber,
            userPassword: userPassword,
            verifyCode: verifyCode
        }
    });
}

async function getAllSportsEvents() {
    return request('/sportsEvent/list', {
        method: 'GET'
    });
}

async function getSportsEventById(eventID) {
    return request('/sportsEvent/get', {
        method: 'POST',
        body: {
            eventID: eventID
        }
    });
}

async function addExerciseRecord(recordData) {
    return request('/exerciseRecord/add', {
        method: 'POST',
        body: {
            userID: recordData.userID,
            sportsDate: recordData.sportsDate,
            eventID: recordData.eventID,
            startTime: recordData.startTime,
            endTime: recordData.endTime,
            exerciseDuration: recordData.exerciseDuration,
            exerciseAmount: recordData.exerciseAmount,
            calorie: recordData.calorie,
            recordType: recordData.recordType
        }
    });
}

async function getExerciseRecordsByUser(userID) {
    return request('/exerciseRecord/listByUser', {
        method: 'POST',
        body: {
            userID: userID
        }
    });
}

async function getExerciseRecordsByDateRange(userID, startDate, endDate) {
    return request('/exerciseRecord/listByDateRange', {
        method: 'POST',
        body: {
            userID: userID,
            startDate: startDate,
            endDate: endDate
        }
    });
}

async function getExerciseRecordById(recordID) {
    return request('/exerciseRecord/get', {
        method: 'POST',
        body: {
            recordID: recordID
        }
    });
}

async function deleteExerciseRecord(recordID) {
    return request('/exerciseRecord/delete', {
        method: 'POST',
        body: {
            recordID: recordID
        }
    });
}

async function createTrainingPlan(planData) {
    return request('/trainingPlan/create', {
        method: 'POST',
        body: {
            planName: planData.planName,
            userID: planData.userID,
            planType: planData.planType,
            startTime: planData.startTime,
            endTime: planData.endTime,
            sportName: planData.sportName,
            exerciseAmount: planData.exerciseAmount,
            percentage: planData.percentage || 0,
            detail: planData.detail || ''
        }
    });
}

async function getTrainingPlansByUser(userID) {
    return request('/trainingPlan/listByUser', {
        method: 'POST',
        body: {
            userID: userID
        }
    });
}

async function getPublishedTrainingPlans() {
    return request('/trainingPlan/listPublished', {
        method: 'POST',
        body: {}
    });
}

async function getTrainingPlanById(planID) {
    return request('/trainingPlan/get', {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

async function updateTrainingPlan(planData) {
    return request('/trainingPlan/update', {
        method: 'POST',
        body: {
            planID: planData.planID,
            planName: planData.planName,
            planType: planData.planType,
            startTime: planData.startTime,
            endTime: planData.endTime,
            sportName: planData.sportName,
            exerciseAmount: planData.exerciseAmount,
            percentage: planData.percentage,
            detail: planData.detail || ''
        }
    });
}

async function deleteTrainingPlan(planID) {
    return request('/trainingPlan/delete', {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

async function completeTrainingPlan(planID) {
    return request('/trainingPlan/complete', {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

async function addPost(postData) {
    return request('/post/create', {
        method: 'POST',
        body: {
            authorID: postData.authorID,
            title: postData.title,
            content: postData.content,
            publishTime: postData.publishTime
        }
    });
}

async function getPosts(page, pageSize) {
    return request('/post/list', {
        method: 'POST',
        body: {
            page: page,
            pageSize: pageSize
        }
    });
}

async function getPostById(postID) {
    return request('/post/get', {
        method: 'POST',
        body: {
            postID: postID
        }
    });
}

async function updatePost(postData) {
    return request('/post/update', {
        method: 'POST',
        body: {
            postID: postData.postID,
            title: postData.title,
            content: postData.content,
            auditState: postData.auditState
        }
    });
}

async function deletePost(postID) {
    return request('/post/delete', {
        method: 'POST',
        body: {
            postID: postID
        }
    });
}

async function auditPost(postID, auditState) {
    return request('/post/audit', {
        method: 'POST',
        body: {
            postID: postID,
            auditState: auditState
        }
    });
}

async function getCommentsByPost(postID) {
    return request('/comment/listByPost', {
        method: 'POST',
        body: {
            postID: postID
        }
    });
}

async function addComment(commentData) {
    return request('/comment/add', {
        method: 'POST',
        body: {
            postID: commentData.postID,
            userID: commentData.userID,
            content: commentData.content
        }
    });
}

async function getPendingPosts() {
    return request('/post/pending', {
        method: 'GET'
    });
}

async function getPendingTrainingPlans() {
    return request('/trainingPlan/getPending', {
        method: 'GET'
    });
}

async function auditTrainingPlan(planID, auditState) {
    const url = auditState === 1 ? '/trainingPlan/auditPass' : '/trainingPlan/auditReject';
    return request(url, {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

async function getPostsByAuthor(authorID) {
    return request(`/post/author/${authorID}`, {
        method: 'GET'
    });
}

async function getCommentById(commentID) {
    return request('/comment/get', {
        method: 'POST',
        body: {
            commentID: commentID
        }
    });
}

async function updateComment(commentData) {
    return request('/comment/update', {
        method: 'POST',
        body: {
            commentID: commentData.commentID,
            content: commentData.content
        }
    });
}

async function deleteComment(commentID) {
    return request('/comment/delete', {
        method: 'POST',
        body: {
            commentID: commentID
        }
    });
}

async function addFavorite(favoriteData) {
    return request('/favorite/add', {
        method: 'POST',
        body: {
            userID: favoriteData.userID,
            targetID: favoriteData.targetID,
            targetType: favoriteData.targetType,
            linkUrl: favoriteData.linkUrl,
            favoriteTime: favoriteData.favoriteTime
        }
    });
}

async function getFavoritesByUser(userID) {
    return request('/favorite/listByUser', {
        method: 'POST',
        body: {
            userID: userID
        }
    });
}

async function deleteFavorite(favoriteID) {
    return request('/favorite/delete', {
        method: 'POST',
        body: {
            favoriteID: favoriteID
        }
    });
}

async function checkFavorite(userID, targetID, targetType) {
    return request('/favorite/check', {
        method: 'POST',
        body: {
            userID: userID,
            targetID: targetID,
            targetType: targetType
        }
    });
}

function setUserSession(userID, userName, userType) {
    localStorage.setItem('userID', userID);
    localStorage.setItem('userName', userName);
    localStorage.setItem('userType', userType);
}

function getUserSession() {
    return {
        userID: localStorage.getItem('userID'),
        userName: localStorage.getItem('userName'),
        userType: localStorage.getItem('userType')
    };
}

function clearUserSession() {
    localStorage.removeItem('userID');
    localStorage.removeItem('userName');
    localStorage.removeItem('userType');
}

function isLoggedIn() {
    return !!localStorage.getItem('userID');
}

window.login = login;
window.register = register;
window.getUserInfo = getUserInfo;
window.updateUserInfo = updateUserInfo;
window.verifyPhone = verifyPhone;
window.sendCode = sendCode;
window.verifyCode = verifyCode;
window.resetPassword = resetPassword;
window.setUserSession = setUserSession;
window.getUserSession = getUserSession;
window.clearUserSession = clearUserSession;
window.isLoggedIn = isLoggedIn;

window.getAllSportsEvents = getAllSportsEvents;
window.getSportsEventById = getSportsEventById;

window.addExerciseRecord = addExerciseRecord;
window.getExerciseRecordsByUser = getExerciseRecordsByUser;
window.getExerciseRecordsByDateRange = getExerciseRecordsByDateRange;
window.getExerciseRecordById = getExerciseRecordById;
window.deleteExerciseRecord = deleteExerciseRecord;

window.createTrainingPlan = createTrainingPlan;
window.getTrainingPlansByUser = getTrainingPlansByUser;
window.getPlansByUserId = getTrainingPlansByUser;
window.getTrainingPlanById = getTrainingPlanById;
window.updateTrainingPlan = updateTrainingPlan;
window.deleteTrainingPlan = deleteTrainingPlan;
window.completeTrainingPlan = completeTrainingPlan;

window.addPost = addPost;
window.getPosts = getPosts;
window.getPostById = getPostById;
window.getPostsByAuthor = getPostsByAuthor;
window.updatePost = updatePost;
window.deletePost = deletePost;
window.auditPost = auditPost;

window.addComment = addComment;
window.getCommentsByPost = getCommentsByPost;
window.getCommentById = getCommentById;
window.updateComment = updateComment;
window.deleteComment = deleteComment;

window.addFavorite = addFavorite;
window.getFavoritesByUser = getFavoritesByUser;
window.deleteFavorite = deleteFavorite;
window.checkFavorite = checkFavorite;
window.checkUserNameExists = checkUserNameExists;

async function auditPlanPass(planID) {
    return request('/trainingPlan/auditPass', {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

async function auditPlanReject(planID) {
    return request('/trainingPlan/auditReject', {
        method: 'POST',
        body: {
            planID: planID
        }
    });
}

window.auditPlanPass = auditPlanPass;
window.auditPlanReject = auditPlanReject;