document.addEventListener("DOMContentLoaded", () => {
    const token = sessionStorage.getItem("Authorization");
    const userId = sessionStorage.getItem("userId");
    const memberContainer = document.getElementById("member-container");
  
    if (token && userId) {
      memberContainer.innerHTML = `
        <div style="display: flex; align-items: center;">
          <b style="margin: 0; font-size: 18px; font:bold;">${userId}님 환영합니다</b>
          <button id="logoutButton" style="margin-left: 10px; background-color: red; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
            로그아웃
          </button>
        </div>
      `;
    } else {
      memberContainer.innerHTML = `<button id="login_button">로그인</button>`;
      document.getElementById("login_button").addEventListener("click", () => {
        window.location.href = "login.html"; // 로그인 페이지로 이동
      });
    }
  });

  document.getElementById("member-container").addEventListener("click", async (e) => {
    if (e.target && e.target.id === "logoutButton") {
        try {
            const userId = sessionStorage.getItem("userId");
            const token = sessionStorage.getItem("Authorization");
            await axios.post("http://localhost:8080/logout", {userId: userId}, {
                headers: {
                    "Authorization": token
                }
            });
            alert("로그아웃 성공");
            sessionStorage.removeItem("Authorization");
            sessionStorage.removeItem("userId");
            location.reload();
        } catch (error) {
            console.error(error);
            alert("로그아웃 실패");
        }
    }
});