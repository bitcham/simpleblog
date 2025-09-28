const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        function success() {
            alert('Deleted successfully.');
            location.replace('/articles');
        }

        function fail() {
            alert('Deletion failed.');
            location.replace('/articles');
        }

        httpRequest('DELETE',`/api/articles/${id}`, null, success, fail);
    });
}


const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        })

        function success() {
            alert('modified successfully.');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('modification failed.');
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT',`/api/articles/${id}`, body, success, fail);
    });
}

const createButton = document.getElementById('create-btn');

if(createButton){
    createButton.addEventListener('click', (e) => {
        body = JSON.stringify({
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    });
    function success(){
        alert('Created successfully.');
        location.replace('/articles');
    }
    function fail(){
        alert('Creation failed.');
        location.replace('/articles');
    }
    httpRequest("POST", "/api/articles", body, success, fail);
    });
}

function getCookie(key){
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item){
        item = item.trim();

        var dic = item.split('=');

        if(key === dic[0]){
            result = dic[1];
            return true;
        }
    });

    return result;
}

function httpRequest(method, url, body, success, fail) {
    const access_token = getCookie('access_token');
    const headers = {
        "Content-Type": "application/json",
    };

    if (access_token) {
        headers.Authorization = 'Bearer ' + access_token;
    }

    fetch(url, {
        method: method,
        headers: headers,
        body: body,
    }).then((response) => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refresh_token: refresh_token,
                }),
            }).then((response) => {
                if (response.status === 200) {
                    return response.json();
                }
            }).then((result) => {
                // Access token is now set in cookie by the server
                httpRequest(method, url, body, success, fail);
            }).catch((error) => fail());
        } else {
            return fail();
        }
    });
}