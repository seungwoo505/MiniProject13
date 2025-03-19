async function downloadFile() {
    const userId = sessionStorage.getItem("userId");
    const token = sessionStorage.getItem("Authorization");
    const detailModal = document.getElementById("fileDetailModal");
    const shareModal = document.getElementById("shareFileDetailModal");

    let fileId;
    if (detailModal && detailModal.style.display === "block") {
        fileId = detailModal.dataset.fileId;


    } else if (shareModal && shareModal.style.display === "block") {
        fileId = shareModal.dataset.fileId;
        try {
            const response = await axios.post(`${BASE_URL}/share/download`,
                { userId: userId, fileId: fileId, shareUser: 1 },
                { headers: { "Authorization": token } });
    
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
                if (response.data.token) {
                    sessionStorage.setItem("Authorization", response.data.token);
                }
            } else {
                alert(response.data.msg);
            }
        } catch (e) {
            alert(e.response.data.msg)
        }



    } else {
        alert("파일 모달이 열려있지 않습니다.");
        return;
    }

    try {
        const response = await axios.post(`${BASE_URL}/download`,
            { userId: userId, fileId: fileId },
            { headers: { "Authorization": token } });

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
            if (response.data.token) {
                sessionStorage.setItem("Authorization", response.data.token);
            }
        } else {
            alert(response.data.msg);
        }
    } catch (e) {
        alert(e.response.data.msg)
    }
}