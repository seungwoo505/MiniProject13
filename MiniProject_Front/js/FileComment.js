  // 파일 상세 모달의 댓글 추가 이벤트
  document.getElementById('addCommentBtn').addEventListener('click', async () => {
    const fileId = document.getElementById("fileDetailModal").dataset.fileId;
    const content = document.getElementById('commentContent').value;
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
    
    if (!content.trim()) {
      alert("댓글 내용을 입력하세요.");
      return;
    }
    
    try {
        const response = await axios.post(`http://localhost:8080/insertComment`, { fileId, writer: userId, comment: content }, { headers: { "Authorization": token }});
        alert(response.data.message);
        sessionStorage.setItem("Authorization", response.data.token);
        document.getElementById('commentContent').value = ''; // 입력 필드 초기화
        loadComments(fileId);
    } catch (error) {
        console.error(error);
        alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
        sessionStorage.removeItem("Authorization");
        sessionStorage.removeItem("userId");
        window.location.reload();
    }
  });

  function loadComments(fileId) {
    axios.post(`http://localhost:8080/selectComment`, { fileId })
      .then(response => {
        const comments = response.data;
        const commentList = document.getElementById('commentList');
        commentList.innerHTML = ''; // 기존 댓글 초기화
        comments.forEach(comment => {
          const commentDiv = document.createElement('div');
          commentDiv.className = 'comment-item';
          commentDiv.innerHTML = `
            <p>${
              comment.comment
                     .replace(/&/g, "&amp;")
                     .replace(/</g, "&lt;")
                     .replace(/>/g, "&gt;")
                     .replace(/"/g, "&quot;")
                     .replace(/'/g, "&#039;")
            }</p>
            <small>작성자: ${comment.writer} / 작성일: ${new Date(comment.createDate).toLocaleString()}</small>
            <br>
            <button class="btn btn-sm btn-danger" onclick="deleteComment(${comment.id}, '${fileId}')">삭제</button>
          `;
          commentList.appendChild(commentDiv);
        });
      })
      .catch(error => console.error(error));
  }
  
  
  // 파일 상세 모달의 댓글 삭제 함수
  async function deleteComment(commentId, fileId) {
    try {
      const userId = sessionStorage.getItem("userId");
      const token = sessionStorage.getItem("Authorization");
      const response = await axios.post(
        "http://localhost:8080/deleteComment",
        { id: commentId, writer: userId },
        { headers: { "Authorization": token } }
      );
      alert(response.data.message);
      sessionStorage.setItem("Authorization", response.data.token);
      loadComments(fileId);
    } catch (error) {
      console.error(error);
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
    }
  }
  
  
  // 공유 파일 모달의 댓글 추가 이벤트
  document.getElementById('shareaddCommentBtn').addEventListener('click', async () => {
    const shareModal = document.getElementById("shareFileDetailModal");
    const fileId = shareModal.dataset.fileId;
    const content = document.getElementById('sharecommentContent').value;
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
  
    if (!content.trim()) {
      alert("댓글 내용을 입력하세요.");
      return;
    }
  
    try {
      const response = await axios.post(
        `http://localhost:8080/insertComment`,
        { fileId, writer: userId, comment: content },
        { headers: { "Authorization": token } }
      );
      alert(response.data.message);
      sessionStorage.setItem("Authorization", response.data.token);
      document.getElementById('sharecommentContent').value = ''; // 입력 필드 초기화
      loadShareComments(fileId);
    } catch (error) {
      console.error(error);
      alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
      sessionStorage.removeItem("Authorization");
      sessionStorage.removeItem("userId");
      window.location.reload();
    }
  });
  
    // 공유 파일 모달의 댓글을 로드하는 함수
    function loadShareComments(fileId) {
        axios.post(`http://localhost:8080/selectComment`, { fileId })
          .then(response => {
            const comments = response.data;
            const commentList = document.getElementById('sharecommentList');
            commentList.innerHTML = ''; // 기존 댓글 초기화
            const currentUser = sessionStorage.getItem("userId");
            comments.forEach(comment => {
              const commentDiv = document.createElement('div');
              commentDiv.className = 'comment-item';
              
              let innerHTML = `<p>${comment.comment}</p>
                               <small>작성자: ${comment.writer} / 작성일: ${new Date(comment.createDate).toLocaleString()}</small>`;
              
              if (currentUser === comment.writer) {
                innerHTML += `<br>
                              <button class="btn btn-sm btn-danger" onclick="deleteShareComment(${comment.id}, '${fileId}')">삭제</button>`;
              }
              
              commentDiv.innerHTML = innerHTML;
              commentList.appendChild(commentDiv);
            });
          })
          .catch(error => console.error(error));
      }
      
  
  // 공유 파일 모달의 댓글 삭제 함수
  async function deleteShareComment(commentId, fileId) {
    try {
        const userId = sessionStorage.getItem("userId");
        const token = sessionStorage.getItem("Authorization");
        const response = await axios.post(
          "http://localhost:8080/deleteComment",
          { id: commentId, writer: userId },
          { headers: { "Authorization": token } }
        );
        alert(response.data.message);
        sessionStorage.setItem("Authorization", response.data.token);
        loadShareComments(fileId);
      } catch (error) {
        console.error(error);
        alert("세션이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인합니다.");
        sessionStorage.removeItem("Authorization");
        sessionStorage.removeItem("userId");
        window.location.reload();
      }
  }