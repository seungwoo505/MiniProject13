document.getElementById("deleteButton").addEventListener("click", async () => {
    if (!confirm("정말 파일을 삭제하시겠습니까?")) return;
  
    // 삭제할 파일의 ID와 사용자 정보 가져오기
    const fileDetailModal = document.getElementById("fileDetailModal");
    const fileId = fileDetailModal.dataset.fileId;
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
  
    try {
      const response = await axios.post(
        `${BASE_URL}/deleteFile`,
        { fileId, userId },
        { headers: { "Authorization": token } }
      );
      sessionStorage.setItem("Authorization", response.data.token);
      alert(response.data.message); 
      fileDetailModal.style.display = "none";
      window.location.reload();
    } catch (error) {
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      console.error("파일 삭제 오류:", error);
      alert("파일 삭제중 오류가 발생했습니다.");
      window.location.reload();
    }
  });
  