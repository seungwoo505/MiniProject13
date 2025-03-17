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

// 파일 업로드 함수
async function uploadFile() {
    const fileInput = document.getElementById("fileInput");
    if (fileInput.files.length === 0) {
        alert("업로드할 파일을 선택하세요.");
        return;
    }

    const userID = sessionStorage.getItem("id");
    const formData = new FormData();
    formData.append("file", fileInput.files[0]);
    formData.append("userId", userID);

    try {
        const response = await axios.post(`${BASE_URL}/upload`, formData);

        alert(response.data);
    } catch (error) {
        console.error("업로드 오류:", error);
    }
}