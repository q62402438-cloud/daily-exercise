const API_BASE_URL = 'http://localhost:8082/api/auth';

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
            return { code: 500, msg: `HTTP错误: ${response.status}` };
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('API请求错误:', error);
        console.error('请求地址:', `${API_BASE_URL}${url}`);
        throw new Error('无法连接到服务器，请检查：\n1. 服务器是否已启动\n2. 服务器端口是否为 8082\n3. 网络连接是否正常');
    }
}

async function login(userName, userPassword, userType = 1) {
    return request('/login', {
        method: 'POST',
        body: {
            userName: userName,
            userPassword: userPassword,
            userType: userType
        }
    });
}

async function register(userData) {
    return request('/register', {
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

async function verifyPhone(phoneNumber) {
    return request('/verifyPhone', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber
        }
    });
}

async function sendCode(phoneNumber) {
    return request('/sendCode', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber
        }
    });
}

async function verifyCode(phoneNumber, code) {
    return request('/verifyCode', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber,
            code: code
        }
    });
}

async function resetPassword(phoneNumber, verifyCode, userPassword) {
    return request('/resetPassword', {
        method: 'POST',
        body: {
            phoneNumber: phoneNumber,
            userPassword: userPassword,
            verifyCode: verifyCode
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
