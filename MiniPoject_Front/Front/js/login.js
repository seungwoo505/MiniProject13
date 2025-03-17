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
    const id = document.getElementById("loginid").value;
    const pwd = document.getElementById("loginpwd").value;
    const data = { id, pwd };
    try {
      const response = await axios.post("http://localhost:8080/tokenlogin", data);
      if (response.data.Authorization) {
        alert(`${id}님 로그인을 환영합니다.`);
        sessionStorage.setItem("Authorization", response.data.Authorization);
        sessionStorage.setItem("id", id);
        window.location.href = "index.html"; // 로그인 성공 시 메인 페이지로 이동
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
    const id = document.getElementById("signupid").value;
    const pwd = document.getElementById("signuppwd").value;
    const data = { id, pwd };
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
