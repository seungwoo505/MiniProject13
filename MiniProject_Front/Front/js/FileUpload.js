document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("fileModal");
    const addFileBtn = document.getElementById("add-file-btn");
    const closeBtn = document.querySelector(".close-upload");
  
    
    // 파일 추가 버튼 클릭 시 모달 열기
    addFileBtn.addEventListener("click", () => {
      modal.style.display = "block";
    });
  
    // 닫기 버튼 클릭 시 모달 닫기
    closeBtn.addEventListener("click", () => {
      modal.style.display = "none";
    });
  
    // 모달 영역 바깥 클릭 시 모달 닫기
    window.addEventListener("click", (event) => {
      if (event.target === modal) {
        modal.style.display = "none";
      }
    });
  });
  
  
  async function downloadFile() {
    // 파일 다운로드 로직
    alert("파일 다운로드 기능 구현 예정");
  }
  
const BASE_URL = "http://localhost:8080";

async function uploadFile() {
  const fileInput = document.getElementById("fileInput");
  if (fileInput.files.length === 0) {
      alert("업로드할 파일을 선택하세요.");
      return;
  }

  const userId = sessionStorage.getItem("userId");
  const token = sessionStorage.getItem("Authorization");
  const formData = new FormData();
  formData.append("file", fileInput.files[0]);
  formData.append("userId", userId);

  try {
      const response = await axios.post(`${BASE_URL}/upload`, formData, {
          headers: {
              "Authorization": token
          }
      });
      alert(response.data.message);
      if (response.data.token) {
          sessionStorage.setItem("Authorization", response.data.token);
      }
  } catch (error) {
      console.error("업로드 오류:", error);
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
  }
}
