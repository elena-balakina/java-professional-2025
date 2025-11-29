let stompClient = null;

const chatLineElementId = "chatLine";
const roomIdElementId = "roomId";
const messageElementId = "message";
const SPECIAL_ROOM_ID = "1408";

const setConnected = (connected) => {
    const connectBtn = document.getElementById("connect");
    const disconnectBtn = document.getElementById("disconnect");

    connectBtn.disabled = connected;
    disconnectBtn.disabled = !connected;
    const chatLine = document.getElementById(chatLineElementId);
    chatLine.hidden = !connected;
    updateSendButtonState();
}

const updateSendButtonState = () => {
    const roomId = document.getElementById(roomIdElementId).value;
    const sendBtn = document.getElementById("send");
    const messageInput = document.getElementById(messageElementId);
    const isRoom1408 = SPECIAL_ROOM_ID === roomId;
    sendBtn.disabled = isRoom1408;
    messageInput.disabled = isRoom1408;
    if (isRoom1408) {
        messageInput.placeholder = "Message sending is not allowed in room 1408";
    } else {
        messageInput.placeholder = "type a message...";
    }
}

const connect = () => {
    stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
    stompClient.connect({}, (frame) => {
        setConnected(true);
        const userName = frame.headers["user-name"];
        const roomId = document.getElementById(roomIdElementId).value;
        console.log(`Connected to roomId: ${roomId} frame:${frame}`);
        const topicName = `/topic/response.${roomId}`;
        const topicNameUser = `/user/${userName}${topicName}`;
        stompClient.subscribe(topicName, (message) => showMessage(JSON.parse(message.body).messageStr));
        stompClient.subscribe(topicNameUser, (message) => showMessage(JSON.parse(message.body).messageStr));
    });
}

// Add event listener for roomId input changes
window.addEventListener('DOMContentLoaded', () => {
    const roomIdInput = document.getElementById(roomIdElementId);
    if (roomIdInput) {
        roomIdInput.addEventListener('input', updateSendButtonState);
        roomIdInput.addEventListener('change', updateSendButtonState);
    }
});

const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

const sendMsg = () => {
    const roomId = document.getElementById(roomIdElementId).value;
    if (SPECIAL_ROOM_ID === roomId) {
        console.log("Message sending is not allowed in room 1408");
        return;
    }
    const message = document.getElementById(messageElementId).value;
    stompClient.send(`/app/message.${roomId}`, {}, JSON.stringify({'messageStr': message}))
}

const showMessage = (message) => {
    const chatLine = document.getElementById(chatLineElementId);
    let newRow = chatLine.insertRow(-1);
    let newCell = newRow.insertCell(0);
    let newText = document.createTextNode(message);
    newCell.appendChild(newText);
}
