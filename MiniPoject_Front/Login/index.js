document.getElementById("signupButton").addEventListener("click", async ()=>{
    const id=document.getElementById("id").value;
    const pwd=document.getElementById("pwd").value;

    const data={id,pwd};

    const response=await axios.post("http://localhost:8080/signup" , data);

    alert(response.data);
});

document.getElementById("loginButton").addEventListener("click", async () => {
    const id = document.getElementById("id").value;
    const pwd = document.getElementById("pwd").value;

    const data = { id, pwd };
  
      const response = await axios.post("http://localhost:8080/tokenlogin", data);
      const token = response.data.Authorization;
      if (response.data.Authorization) {
        alert(id+"님 로그인을 환영영합니다.");
        sessionStorage.setItem('Authorization', token);
        sessionStorage.setItem('id', id);
         // 로그인 폼이 들어있는 컨테이너를 찾아 innerHTML 업데이트
         const container = document.getElementById("login-container");
         container.innerHTML = `
             <h2>${id}님 환영합니다.</h2>
             <button id="logoutButton">로그아웃</button>
        `;
      } else {
        alert(response.data.msg);
      }

  });

document.getElementById("login-container").addEventListener("click", async (e) => {
    if (e.target && e.target.id === "logoutButton") {
        try {
            const id = sessionStorage.getItem("id");
            const token = sessionStorage.getItem("Authorization");
            await axios.post("http://localhost:8080/logout", {id: id}, {
                headers: {
                    "Authorization": token
                }
            });
            alert("로그아웃 성공");
            sessionStorage.removeItem("Authorization");
            sessionStorage.removeItem("id");
            location.reload();
        } catch (error) {
            console.error(error);
            alert("로그아웃 실패");
        }
    }
});

window.addEventListener("DOMContentLoaded", () => {
    const token = sessionStorage.getItem("Authorization");
    const id = sessionStorage.getItem("id");

    if (token && id) {
      axios.defaults.headers.common["Authorization"] = token;
      const container = document.getElementById("login-container");
      container.innerHTML = `
        <h2>${id}님 환영합니다.</h2>
        <button id="logoutButton">로그아웃</button>
      `;
    }
  });