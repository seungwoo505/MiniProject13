document.addEventListener("DOMContentLoaded", async () => {
  const userId = sessionStorage.getItem("userId");
  const token = sessionStorage.getItem("Authorization");
  if (!userId || !token) {
    return;
  }

  //로그인 후 내 문서함 열기
  try {
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
    const response = await axios.post(
      `${BASE_URL}/getFiles`,
      { userId: userId },
      { headers: { "Authorization": token } }
    );
    const result = response.data;

    if (!result[0].token) {
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
      return;
    }
    
    sessionStorage.setItem("Authorization", result[0].token);
    console.log("새로운 토큰 저장됨:", result[0].token);

    if (!result[0].fileId) {
      renderFileList([]); // 빈 파일 목록 렌더링
    }else{
      renderFileList(result);
    }
    

  } catch (error) {
    console.error("파일 목록 가져오기 오류:", error);
    alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
    sessionStorage.removeItem("Authorization");
    sessionStorage.removeItem("userId");
    window.location.reload();
  }

  // 탭 요소
  const myDocsLink = document.getElementById("myDocsLink");
  const sharedDocsLink = document.getElementById("sharedDocsLink");

  // 내 문서함 클릭 시
  myDocsLink.addEventListener("click", async (e) => {
    myDocsLink.classList.add("active");
    sharedDocsLink.classList.remove("active");

    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
    e.preventDefault();
    try {
      const response = await axios.post(
        `${BASE_URL}/getFiles`,
        { userId: userId },
        { headers: { "Authorization": token } }
      );
      const result = response.data;
      if (!result[0].token) {
        alert("세션이 만료되었거나 토큰이 유효하지 않습니다.");
        sessionStorage.removeItem("Authorization");
        sessionStorage.removeItem("userId");
        window.location.reload();
        return;
      }
      sessionStorage.setItem("Authorization", result[0].token);
      console.log("새로운 토큰 저장됨:", result[0].token);

      if (!result[0].fileId) {
        renderFileList([]);
      }else{
        renderFileList(result);
      }
      
    } catch (error) {
      console.error("내 문서함 파일 가져오기 오류:", error);
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
    }
  });

  // 공유 문서함 클릭 시
  sharedDocsLink.addEventListener("click", async (e) => {
    sharedDocsLink.classList.add("active");
    myDocsLink.classList.remove("active");
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
    e.preventDefault();
    try {
      const response = await axios.post(
        `${BASE_URL}/share/getShareFiles`,
        { userId: userId },
        { headers: { "Authorization": token } }
      );
      const result = response.data;
      console.log(response);
      if (!result[0].token) {
        alert("세션이 만료되었거나 토큰이 유효하지 않습니다.");
        sessionStorage.removeItem("Authorization");
        sessionStorage.removeItem("userId");
        window.location.reload();
        return;
      }
      sessionStorage.setItem("Authorization", result[0].token);
      console.log("새로운 토큰 저장됨:", result[0].token);
      console.log(result[0].fileId);
      if (!result[0].fileId) {
        renderFileList([]);
      }else{
        renderFileList(result);
      }
    } catch (error) {
      console.error("공유 문서함 파일 가져오기 오류:", error);
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
    }
  });

  // 상세 모달 닫기
  const detailModal = document.getElementById("fileDetailModal");
  const closeDetailBtn = document.querySelector(".close-file-detail");
  closeDetailBtn.addEventListener("click", () => {
    detailModal.style.display = "none";
  });
  window.addEventListener("click", (event) => {
    if (event.target === detailModal) {
      detailModal.style.display = "none";
    }
  });


  document.getElementById("shareButton").addEventListener("click", () => {
    alert("공유하는 기능 구현 예정");
  });

  document.getElementById("generateLinkBtn").addEventListener("click", () => {
    const userId = sessionStorage.getItem("userId");
    const fileId = document.getElementById("fileDetailModal").dataset.fileId;

    const shareIds = document.getElementById('shareList').innerText.trim().split("삭제");

    const shareUser = !shareIds.every(item => item === "");
    
    axios.post('http://localhost:8080/share/create', { userId, fileId, shareIds, shareUser})
      .then(response => {
        console.log(response.data);
        alert(`공유 성공! : ${response.data}`);
        loadShareList(fileId);
      })
      .catch(error => {
        alert('공유에 실패했습니다.');
        console.error('공유 실패:', error);
      });
  });

});

// 파일 리스트 렌더링
function renderFileList(fileData) {
  const fileListContainer = document.getElementById("file-list");
  fileListContainer.innerHTML = ""; // 기존 내용 초기화

  fileData.forEach((file, index) => {
    const fileItem = document.createElement("div");
    fileItem.classList.add("file-item");

    const nameDiv = document.createElement("div");
    nameDiv.classList.add("file-item-name");
    nameDiv.textContent = file.fileName;
    fileItem.appendChild(nameDiv);

    const extension = file.fileName.split('.').pop().toLowerCase();
    const imageExtensions = ['png', 'jpg', 'jpeg', 'gif'];
    const previewDiv = document.createElement("div");
    previewDiv.classList.add("file-item-preview");
    
    if (imageExtensions.includes(extension)) {
      preview(file.fileId, file.userId, index, "img");
    }else if(["txt"].includes(extension)){
      preview(file.fileId, file.userId, index, "text");
    }
    fileItem.appendChild(previewDiv);

    // 파일 클릭 시, 공유 파일인 경우와 내 파일인 경우 구분 (예: 공유 문서함 탭에 따라)
    const sharedDocsLink = document.getElementById("sharedDocsLink");
    if (sharedDocsLink && sharedDocsLink.classList.contains("active")) {
      fileItem.addEventListener("click", () => {
        openShareFileDetailModal(file);
      });
    } else {
      fileItem.addEventListener("click", () => {
        openFileDetailModal(file);
      });
    }

    fileListContainer.appendChild(fileItem);
  });
}

// 날짜 포맷 함수
function formatDate(dateStr) {
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 파일 상세 모달 열기
function openFileDetailModal(file) {
  const detailModal = document.getElementById("fileDetailModal");
  const formattedUploadDate = formatDate(file.uploadDate);
  const formattedUpdateDate = formatDate(file.updateDate);
  detailModal.dataset.fileId = file.fileId;
  detailModal.style.display = "block";
  document.getElementById("detailFileName").textContent = file.fileName;
  document.getElementById("detailFileDates").textContent =
    `업로드: ${formattedUploadDate} / 업데이트: ${formattedUpdateDate}`;
  document.getElementById("detailFileOwner").textContent = `소유자: ${file.userId}`;

  loadShareList(file.fileId);
}

// 공유 파일 상세 모달 열기
function openShareFileDetailModal(file) {
  // 공유 파일 전용 모달을 가져옴
  const shareModal = document.getElementById("shareFileDetailModal");
  shareModal.style.display = "block";
  
  // 파일 상세 정보 포맷팅
  const formattedUploadDate = formatDate(file.uploadDate);
  const formattedUpdateDate = formatDate(file.updateDate);
  shareModal.dataset.fileId = file.fileId;
  shareModal.dataset.ownerId = file.userId;
  
  // 공유 전용 모달에 파일 정보 채우기
  document.getElementById("shareDetailFileName").textContent = file.fileName;
  document.getElementById("shareDetailFileDates").textContent =
    `업로드: ${formattedUploadDate} / 업데이트: ${formattedUpdateDate}`;
  document.getElementById("shareDetailFileOwner").textContent = `소유자: ${file.userId}`;

  shareFileId = file.fileId;
  
  // 공유 파일 전용 모달 닫기 버튼 이벤트 설정
  const closeBtn = document.querySelector(".close-share-file-detail");
  closeBtn.addEventListener("click", () => {
    shareModal.style.display = "none";
  });
  
  // 필요에 따라, 모달 외부 클릭 시 닫기 처리도 추가
  window.addEventListener("click", (event) => {
    if (event.target === shareModal) {
      shareModal.style.display = "none";
    }
  });
}

function loadShareList(fileId) {
  axios.post('http://localhost:8080/share/shareFile', {fileId})
    .then(response => {
      const shares = response.data;
      const shareList = document.getElementById('shareList');
      shareList.innerHTML = '';
      shares.forEach(share => {
        console.log(share);
        const li = document.createElement('li');
        li.className = 'list-group-item';
        li.textContent = share;

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "삭제";
        deleteBtn.className = "btn btn-danger btn-sm ms-2"; // Bootstrap 스타일 적용 가능
        deleteBtn.addEventListener("click", () => {
          shareList.removeChild(li); // 리스트에서 해당 요소 삭제
        });
        li.appendChild(deleteBtn);
        shareList.appendChild(li);
      });
    })
    .catch(error => {
      console.error('공유 대상 목록 로드 실패:', error);
    });
}

document.getElementById("shareTarget").addEventListener("keydown", (e) => {
  if(e.key === "Enter"){
    const shareId = e.target.value; 

    const shareList = document.getElementById('shareList');
    const li = document.createElement('li');
    li.className = 'list-group-item';
    li.textContent = shareId;

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "삭제";
    deleteBtn.className = "btn btn-danger btn-sm ms-2"; // Bootstrap 스타일 적용 가능
    deleteBtn.addEventListener("click", () => {
      shareList.removeChild(li); // 리스트에서 해당 요소 삭제
    });
    li.appendChild(deleteBtn);
    shareList.appendChild(li);
    e.target.value = "";
  }
});

function base64ToBlob(base64, mimeType) {
  const byteCharacters = atob(base64);
  const byteNumbers = new Array(byteCharacters.length);
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i);
  }
  const byteArray = new Uint8Array(byteNumbers);
  return new Blob([byteArray], { type: mimeType });
}

function preview(fileId, userId, index, previewType) {
  const data = {
    fileId : fileId,
    userId : userId,
    type : previewType
  };
      
  if (previewType === 'img') {
    axios.post(`http://localhost:8080/preview`, data)
         .then(response => {
            const data = response.data;
            // data.file가 base64 문자열이어야 함
            if (typeof data.file === 'string') {
              // MIME 타입은 실제 파일 형식에 맞게 설정 (여기서는 image/jpeg로 가정)
              const blob = base64ToBlob(data.file, "image/jpeg");
              const imageUrl = URL.createObjectURL(blob);
              const preview = document.getElementsByClassName('file-item-preview')[index];
              const img = document.createElement("img");
              img.src = imageUrl;
              img.alt = "썸네일";
              preview.appendChild(img);
            }else {
              throw new Error("응답 데이터 형식이 올바르지 않습니다.");
            }
          })
          .catch(error => {
            console.error("이미지 미리보기 오류:", error);
            alert("이미지 미리보기를 가져오는 중 오류가 발생했습니다.");
          });
  } else if (previewType === 'text') {
        // 텍스트 미리보기: 응답 데이터가 문자열로 전달된다고 가정
        axios.post(`http://localhost:8080/preview`, data)
          .then(response => {
            const text = response.data.file;
            const preview = document.getElementsByClassName('file-item-preview')[index];
            const p = document.createElement("p");
            p.textContent = text;
            preview.appendChild(p);
            //document.getElementById('previewImage').style.display = "none";
          })
          .catch(error => {
            console.error("텍스트 미리보기 오류:", error);
            alert("텍스트 미리보기를 가져오는 중 오류가 발생했습니다.");
          });
      }
}