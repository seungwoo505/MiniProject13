// 테스트용 "내 문서함" 파일 배열
const myFileData = [
  { id: 1, name: "파일1", owner: "홍길동", type: "pdf" },
  { id: 2, name: "파일2", owner: "홍길동", type: "image" , thumbnail: "image.jpg"}
];

// 테스트용 "공유 문서함" 파일 배열
const sharedFileData = [
  { id: 3, name: "파일3", owner: "홍길동", type: "pdf" },
  { id: 4, name: "파일4", owner: "홍길동", type: "pdf" }
];

document.addEventListener("DOMContentLoaded", () => {
  // 기본으로 "내 문서함" 렌더링
  renderFileList(myFileData);

  // 탭 요소
  const myDocsLink = document.getElementById("myDocsLink");
  const sharedDocsLink = document.getElementById("sharedDocsLink");

  // 내 문서함 클릭 시
  myDocsLink.addEventListener("click", (e) => {
    e.preventDefault();
    renderFileList(myFileData);
    myDocsLink.classList.add("active");
    sharedDocsLink.classList.remove("active");
  });

  // 공유 문서함 클릭 시
  sharedDocsLink.addEventListener("click", (e) => {
    e.preventDefault();
    renderFileList(sharedFileData);
    sharedDocsLink.classList.add("active");
    myDocsLink.classList.remove("active");
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

  // 다운로드, 공유, 링크 생성 버튼 이벤트
  document.getElementById("downloadFileBtn").addEventListener("click", () => {
    alert("다운로드 기능 구현 예정");
  });
  document.getElementById("shareButton").addEventListener("click", () => {
    alert("공유하는 기능 구현 예정");
  });
  document.getElementById("generateLinkBtn").addEventListener("click", () => {
    alert("링크 생성 기능 구현 예정");
  });
});

// 파일 리스트 렌더링 (구글 드라이브 스타일)
function renderFileList(fileData) {
  const fileListContainer = document.getElementById("file-list");
  fileListContainer.innerHTML = ""; // 기존 내용 초기화

  fileData.forEach(file => {
    const fileItem = document.createElement("div");
    fileItem.classList.add("file-item");

    const nameDiv = document.createElement("div");
    nameDiv.classList.add("file-item-name");
    nameDiv.textContent = file.name;
    fileItem.appendChild(nameDiv);

    // 미리보기 사진일 경우
    const previewDiv = document.createElement("div");
    previewDiv.classList.add("file-item-preview");
    if (file.type === "image" && file.thumbnail) {
      const img = document.createElement("img");
      img.src = file.thumbnail;
      img.alt = "썸네일";
      previewDiv.appendChild(img);
    }
    fileItem.appendChild(previewDiv);

    // 파일 상세 모달 
    fileItem.addEventListener("click", () => {
      openFileDetailModal(file);
    });

    fileListContainer.appendChild(fileItem);
  });
}

// 파일 상세 모달 열기
function openFileDetailModal(file) {
  const detailModal = document.getElementById("fileDetailModal");
  detailModal.style.display = "block";
  document.getElementById("detailFileName").textContent = file.name;
  document.getElementById("detailFileOwner").textContent = "소유자: " + file.owner;
}
