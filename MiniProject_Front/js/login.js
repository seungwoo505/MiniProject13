document.addEventListener("DOMContentLoaded", () => {
  // 탭 전환 처리
  const tabLinks = document.querySelectorAll(".tab-link");
  const tabContents = document.querySelectorAll(".tab-content");
  tabLinks.forEach(link => {
    link.addEventListener("click", () => {
      tabLinks.forEach(btn => btn.classList.remove("active"));
      tabContents.forEach(content => content.classList.remove("active"));
      link.classList.add("active");
      const tabId = link.getAttribute("data-tab");
      document.getElementById(tabId).classList.add("active");
    });
  });

  // 로그인 폼 엔터키 처리
  document.getElementById("loginpwd").addEventListener("keydown", event => {
    if (event.key === "Enter") {
      event.preventDefault();
      document.getElementById("loginButton").click();
    }
  });
  // 회원가입 폼 엔터키 처리
  document.getElementById("signuppwd").addEventListener("keydown", event => {
    if (event.key === "Enter") {
      event.preventDefault();
      document.getElementById("signupButton").click();
    }
  });

  // 로그인 버튼 클릭
  document.getElementById("loginButton").addEventListener("click", async () => {
    const userId = document.getElementById("loginid").value;
    const pwd = document.getElementById("loginpwd").value;
    const data = { userId, pwd };

      if (!isValidId(userId) || !isValidPassword(pwd)) {
        return;
      }

    try {
      const response = await axios.post("http://localhost:8080/tokenlogin", data);
      if (response.data.Authorization) {
        alert(`${userId}님 로그인을 환영합니다.`);
        sessionStorage.setItem("Authorization", response.data.Authorization);
        sessionStorage.setItem("userId", userId);
        //window.location.href = "index.html"; // 로그인 성공 시 메인 페이지로 이동
        console.log(document.referrer);
        if(document.referrer === "http://127.0.0.1:5500/" || document.referrer === "http://127.0.0.1:5500/index.html" || document.referrer.indexOf("http://127.0.0.1:5500/share/index.html") !== -1){
          window.location = document.referrer;
        }else{
          window.location.href = "index.html"; // 로그인 성공 시 메인 페이지로 이동
        }
      } else {
        alert(response.data.msg || "로그인 실패");
      }
    } catch (error) {
      console.error(error);
      alert("로그인 오류");
    }
  });

  // 회원가입 버튼 클릭
  document.getElementById("signupButton").addEventListener("click", async () => {
    const userId = document.getElementById("signupid").value;
    const pwd = document.getElementById("signuppwd").value;
    const data = { userId, pwd };

    if (!isValidId(userId) || !isValidPassword(pwd)) {
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/signup", data);
      const msg = response.data;
      alert(msg);
      if (msg.includes("님 가입을 환영합니다")) {
        window.location.href = "login.html"; // 회원가입 성공 시 로그인 페이지로 이동
      }
    } catch (error) {
      console.error(error);
      alert("회원가입 오류");
    }
  });
});

// 유효성 검사 함수
function isValidId(userId) {
  const idRegex = /^[A-Za-z0-9]{5,}$/;
  return idRegex.test(userId);
}

function isValidPassword(pw) {
  const pwRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/;
  return pwRegex.test(pw);
}

// 회원가입 폼 실시간 유효성 검사
const signupIdInput = document.getElementById("signupid");
const signupPwdInput = document.getElementById("signuppwd");
const loginIdInput = document.getElementById("loginid");
const loginPwdInput = document.getElementById("loginpwd");
const loginIdError = document.getElementById("login-id-error");
const loginPwdError = document.getElementById("login-pwd-error");
const signupIdError = document.getElementById("signup-id-error");
const signupPwdError = document.getElementById("signup-pwd-error");

signupIdInput.addEventListener("input", () => {
  if (!isValidId(signupIdInput.value)) {
    signupIdError.textContent = "아이디는 5글자 이상, 영어와 숫자만 사용 가능";
  } else {
    signupIdError.textContent = "";
  }
});

signupPwdInput.addEventListener("input", () => {
  if (!isValidPassword(signupPwdInput.value)) {
    signupPwdError.textContent = "비밀번호는 8자리 이상, 최소 하나의 숫자와 특수문자를 포함";
  } else {
    signupPwdError.textContent = "";
  }
});

loginIdInput.addEventListener("input", () => {
  if (!isValidId(loginIdInput.value)) {
    loginIdError.textContent = "아이디는 5글자 이상, 영어와 숫자만 사용 가능";
  } else {
    loginIdError.textContent = "";
  }
});

loginPwdInput.addEventListener("input", () => {
  if (!isValidPassword(loginPwdInput.value)) {
    loginPwdError.textContent = "비밀번호는 8자리 이상, 최소 하나의 숫자와 특수문자를 포함";
  } else {
    loginPwdError.textContent = "";
  }
});
