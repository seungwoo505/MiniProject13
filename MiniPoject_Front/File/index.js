const BASE_URL = "http://localhost:8080";

// 파일 업로드 함수
async function uploadFile() {
    const fileInput = document.getElementById("fileInput");
    if (fileInput.files.length === 0) {
        alert("업로드할 파일을 선택하세요.");
        return;
    }

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);
    formData.append("userId", "이승우");

    try {
        const response = await axios.post(`${BASE_URL}/upload`, formData);

        alert(response.data);
    } catch (error) {
        console.error("업로드 오류:", error);
    }
}

// 파일 다운로드 함수
async function downloadFile() {
    const fileName = document.getElementById("fileName").value;
    if (!fileName) {
        alert("파일 이름을 입력하세요.");
        return;
    }

    try {
        const response = await axios.post(`${BASE_URL}/download`, { fileId : fileName, userId : "이승우" })

        console.log(response);

        if (response.status === 200) {
            const { fileName, file } = response.data;
            const blob = new Blob([new Uint8Array(atob(file).split('').map(char => char.charCodeAt(0)))], { type: 'application/octet-stream' });
            const downloadUrl = window.URL.createObjectURL(blob);
            const a = document.createElement("a");

            a.href = downloadUrl;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert("파일 다운로드 실패!");
        }
    } catch (error) {
        console.error("다운로드 오류:", error);
    }
}
