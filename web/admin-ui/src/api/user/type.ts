// 登录参数
export interface loginForm {
    username: string;
    password: string;
}
// 定义登录数据类型
export interface LoginData {
    // 这里根据实际情况定义登录数据的字段
    username: string;
    password: string;
}

// 定义登录响应类型
export interface LoginResponse {
    code: number;
    data: string;
    msg: string;
}