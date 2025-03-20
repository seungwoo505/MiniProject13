// BASE_URL 설정
const BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("fileModal");
    const addFileBtn = document.getElementById("add-file-btn");
    const closeBtn = document.querySelector(".close-upload");
    const dropZone = document.getElementById("dropZone");
    const uploadBtn = document.getElementById("uploadBtn");
    const resultDiv = document.getElementById("result");
    
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

    // 드래그 앤 드롭 관련 변수
  let droppedFiles = [];

  // 드래그 앤 드롭 이벤트 기본 동작 방지
  ["dragenter", "dragover", "dragleave", "drop"].forEach((eventName) => {
    dropZone.addEventListener(eventName, (e) => {
      e.preventDefault();
      e.stopPropagation();
    }, false);
  });

  // 드래그 오버 시 스타일 추가/제거
  dropZone.addEventListener("dragover", () => {
    dropZone.classList.add("hover");
  }, false);
  dropZone.addEventListener("dragleave", () => {
    dropZone.classList.remove("hover");
  }, false);

  // 파일 드롭 시 처리: 여러 파일을 배열에 저장 및 파일 이름 표시
  dropZone.addEventListener("drop", (e) => {
    dropZone.classList.remove("hover");
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      droppedFiles = Array.from(files);
      if(files.length > 5){
        alert("5개까지 가능합니다.");
      }
      droppedFiles = droppedFiles.slice(0, 5);

      for(let f = 0; f < droppedFiles.length; f++){
        let file = droppedFiles[f];
        if(file){
          const size = (file.size / (1024 * 1024)).toFixed(2);

          if(size > 20){
            alert("최대 용량은 20MB입니다.");
            droppedFiles[f] = "";
          }
        }
      }
      
      
      const fileNames = droppedFiles.map(file => file.name).join(', ');
      dropZone.textContent = "선택된 파일들: " + fileNames;
    }
  }, false);

  // 파일 업로드 버튼 클릭 시: 업로드할 파일이 있는지 확인 후 FormData로 전송
  uploadBtn.addEventListener("click", async () => {
    if (droppedFiles.length === 0) {
      alert("업로드할 파일을 드래그 앤 드롭 하세요.");
      return;
    }

    const userId = sessionStorage.getItem("userId");
    let token = sessionStorage.getItem("Authorization");
  
    for (const file of droppedFiles) {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("userId", userId);
      
      try {
        const response = await axios.post(`${BASE_URL}/upload`, formData, {
          headers: {"Authorization": sessionStorage.getItem("Authorization")}
        });
        resultDiv.innerHTML = `<div class="alert alert-success">업로드 성공: ${response.data.message}</div>`;
        // 토큰 갱신
        console.log(response.data.token)
        sessionStorage.setItem("Authorization", response.data.token);
        token = sessionStorage.getItem("Authorization");
      } catch (error) {
        console.error("업로드 오류:", error);
        const errorMsg = error.response && error.response.data ? error.response.data : error.message;
        resultDiv.innerHTML = `<div class="alert alert-danger">업로드 실패: ${errorMsg}</div>`;
      }
    }
  });
});
