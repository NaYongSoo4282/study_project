const state = {
    token: localStorage.getItem("eventPlatformToken") || "",
    currentMode: "admin",
    selectedEvent: null,
    selectedApplication: null,
    adminEvents: [],
    memberEvents: [],
    eventLists: {}
};

const $ = (id) => document.getElementById(id);

const els = {
    apiBase: $("apiBase"),
    token: $("token"),
    connectionState: $("connectionState"),
    profileSummary: $("profileSummary"),
    workspaceTitle: $("workspaceTitle"),
    selectedEventLabel: $("selectedEventLabel"),
    selectedApplicationLabel: $("selectedApplicationLabel"),
    toast: $("toast")
};

els.token.value = state.token;

document.querySelectorAll(".mode-btn").forEach((button) => {
    button.addEventListener("click", () => switchMode(button.dataset.mode));
});

document.querySelectorAll(".tab").forEach((button) => {
    button.addEventListener("click", () => switchTab(button.dataset.scope, button.dataset.tab));
});

document.querySelectorAll("[data-action]").forEach((button) => {
    button.addEventListener("click", () => runAction(button.dataset.action));
});

document.querySelectorAll("[data-event-picker]").forEach((button) => {
    button.addEventListener("click", () => loadEventPicker(button));
});

document.querySelectorAll("[data-status]").forEach((button) => {
    button.addEventListener("click", () => updateEventStatus(button.dataset.status));
});

$("saveTokenBtn").addEventListener("click", () => {
    saveToken(els.token.value.trim());
    showToast("토큰을 저장했습니다.");
});

$("clearTokenBtn").addEventListener("click", () => {
    saveToken("");
    els.profileSummary.textContent = "로그인 정보가 없습니다.";
    els.profileSummary.classList.add("empty");
    setConnection("대기", "neutral");
    showToast("토큰을 초기화했습니다.");
});

$("loadProfileBtn").addEventListener("click", loadProfile);

if (state.token) {
    loadProfile({ silent: true });
}

function switchMode(mode) {
    state.currentMode = mode;
    document.querySelectorAll(".mode-btn").forEach((button) => {
        button.classList.toggle("active", button.dataset.mode === mode);
    });
    document.querySelectorAll(".mode-panel").forEach((panel) => {
        panel.classList.toggle("active", panel.id === `mode-${mode}`);
    });
    els.workspaceTitle.textContent = mode === "admin" ? "관리자 화면" : "일반회원 화면";
}

function switchTab(scope, tab) {
    document.querySelectorAll(`.tab[data-scope="${scope}"]`).forEach((button) => {
        button.classList.toggle("active", button.dataset.tab === tab);
    });
    document.querySelectorAll(`#mode-${scope} .tab-panel`).forEach((panel) => {
        panel.classList.toggle("active", panel.id === `${scope}-${tab}`);
    });
}

function baseUrl() {
    return els.apiBase.value.trim().replace(/\/$/, "");
}

function saveToken(token) {
    state.token = token;
    els.token.value = token;
    if (token) {
        localStorage.setItem("eventPlatformToken", token);
    } else {
        localStorage.removeItem("eventPlatformToken");
    }
}

function authHeaders(hasBody) {
    const headers = {};
    const token = els.token.value.trim();

    if (hasBody) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    return headers;
}

async function request(method, path, body) {
    const hasBody = body !== undefined;
    const response = await fetch(`${baseUrl()}${path}`, {
        method,
        headers: authHeaders(hasBody),
        body: hasBody ? JSON.stringify(body) : undefined
    });

    const text = await response.text();
    let payload = null;

    try {
        payload = text ? JSON.parse(text) : null;
    } catch (error) {
        payload = { message: text };
    }

    if (!response.ok) {
        const message = payload?.message || payload?.code || `${response.status} 요청 실패`;
        throw new Error(message);
    }

    return payload;
}

async function runAction(action) {
    try {
        setConnection("처리 중", "neutral");

        const actions = {
            adminSignup,
            adminLogin,
            memberSignup,
            memberLogin,
            loadProfile,
            loadDashboard,
            loadMembers,
            loadAdminEvents,
            loadMemberEvents,
            newEventForm,
            createEvent,
            updateEvent,
            deleteEvent,
            loadApplications,
            createAttendanceCode,
            loadAttendances,
            loadEventFeedbacks,
            loadAllFeedbacks,
            applyEvent,
            loadMyApplication,
            cancelMyApplication,
            checkAttendance,
            createFeedback
        };

        if (!actions[action]) {
            throw new Error(`알 수 없는 작업입니다: ${action}`);
        }

        await actions[action]();
        setConnection("정상", "success");
    } catch (error) {
        setConnection("오류", "danger");
        showToast(error.message, true);
    }
}

function setConnection(text, tone) {
    els.connectionState.textContent = text;
    els.connectionState.className = `pill ${tone}`;
}

function showToast(message, isError = false) {
    els.toast.textContent = message;
    els.toast.classList.toggle("error", isError);
    els.toast.classList.add("show");
    window.clearTimeout(showToast.timer);
    showToast.timer = window.setTimeout(() => {
        els.toast.classList.remove("show");
    }, 2600);
}

async function adminSignup() {
    await signup({
        email: $("adminSignupEmail").value,
        password: $("adminSignupPassword").value,
        name: $("adminSignupName").value,
        role: "ADMIN"
    });
    showToast("관리자 계정을 만들었습니다.");
}

async function memberSignup() {
    await signup({
        email: $("memberSignupEmail").value,
        password: $("memberSignupPassword").value,
        name: $("memberSignupName").value,
        role: "MEMBER"
    });
    showToast("회원 계정을 만들었습니다.");
}

async function signup(body) {
    await request("POST", "/api/v1/auth/signup", body);
}

async function adminLogin() {
    await login($("adminLoginEmail").value, $("adminLoginPassword").value);
}

async function memberLogin() {
    await login($("memberLoginEmail").value, $("memberLoginPassword").value);
}

async function login(email, password) {
    const payload = await request("POST", "/api/v1/auth/login", { email, password });
    const token = payload?.data?.accessToken;
    if (!token) {
        throw new Error("로그인 응답에서 토큰을 찾지 못했습니다.");
    }
    saveToken(token);
    await loadProfile({ silent: true });
    showToast("로그인했습니다.");
}

async function loadProfile(options = {}) {
    const payload = await request("GET", "/api/v1/members/me");
    const member = payload.data;
    els.profileSummary.classList.remove("empty");
    els.profileSummary.innerHTML = `
        <strong>${escapeHtml(member.name)}</strong>
        <span class="pill ${member.role === "ADMIN" ? "success" : "neutral"}">${member.role}</span>
        <div>${escapeHtml(member.email)}</div>
    `;
    if (!options.silent) {
        showToast("내 정보를 불러왔습니다.");
    }
}

async function loadDashboard() {
    const payload = await request("GET", "/api/v1/admin/dashboard");
    const data = payload.data;
    $("dashboardCards").innerHTML = [
        metric("전체 행사", data.totalEventCount),
        metric("모집 중 행사", data.openEventCount),
        metric("전체 신청", data.totalApplicationCount),
        metric("승인된 신청", data.approvedApplicationCount),
        metric("출석", data.attendanceCount),
        metric("피드백", data.feedbackCount)
    ].join("");
    showToast("대시보드를 갱신했습니다.");
}

function metric(label, value) {
    return `<div class="metric-card"><span>${label}</span><strong>${value}</strong></div>`;
}

async function loadMembers() {
    const payload = await request("GET", "/api/v1/admin/members");
    const members = payload.data || [];
    $("memberTable").classList.remove("empty-state");
    $("memberTable").innerHTML = renderTable(
        ["ID", "이름", "이메일", "권한"],
        members.map((member) => [
            member.id,
            escapeHtml(member.name),
            escapeHtml(member.email),
            badge(member.role)
        ]),
        "회원이 없습니다."
    );
    showToast("회원 목록을 불러왔습니다.");
}

async function loadAdminEvents() {
    const events = await loadEvents($("adminEventStatusFilter").value);
    state.adminEvents = events;
    renderEventList("adminEventList", events, "admin");
    showToast("행사 목록을 불러왔습니다.");
}

async function loadMemberEvents() {
    const events = await loadEvents($("memberEventStatusFilter").value);
    state.memberEvents = events;
    renderEventList("memberEventList", events, "member");
    showToast("행사 목록을 불러왔습니다.");
}

async function loadEventPicker(button) {
    try {
        setConnection("처리 중", "neutral");
        const targetId = button.dataset.target;
        const filterId = button.dataset.filter;
        const scope = button.dataset.scope;
        const events = await loadEvents(filterId ? $(filterId).value : "");
        renderEventList(targetId, events, scope);
        setConnection("정상", "success");
        showToast("행사 선택 목록을 불러왔습니다.");
    } catch (error) {
        setConnection("오류", "danger");
        showToast(error.message, true);
    }
}

async function loadEvents(status) {
    const query = status ? `?status=${encodeURIComponent(status)}` : "";
    const payload = await request("GET", `/api/v1/events${query}`);
    return payload.data || [];
}

function renderEventList(targetId, events, scope) {
    const target = $(targetId);
    state.eventLists[targetId] = { events, scope };
    if (!events.length) {
        target.className = "event-list empty-state";
        target.textContent = "조건에 맞는 행사가 없습니다.";
        return;
    }

    target.className = "event-list";
    target.innerHTML = events.map((event) => `
        <button class="event-item ${state.selectedEvent?.id === event.id ? "active" : ""}" type="button" data-event-id="${event.id}" data-scope="${scope}" data-target-id="${targetId}">
            <div class="card-heading">
                <h4>${escapeHtml(event.title)}</h4>
                ${badge(event.status)}
            </div>
            <div class="event-meta">
                <span>${formatDate(event.startAt)} - ${formatDate(event.endAt)}</span>
                <span>${escapeHtml(event.location || "장소 미정")} · 정원 ${event.capacity ?? "-"}</span>
            </div>
        </button>
    `).join("");

    target.querySelectorAll(".event-item").forEach((button) => {
        button.addEventListener("click", () => selectEvent(Number(button.dataset.eventId), button.dataset.scope, button.dataset.targetId));
    });
}

async function selectEvent(eventId, scope, targetId) {
    const payload = await request("GET", `/api/v1/events/${eventId}`);
    state.selectedEvent = payload.data;
    state.selectedApplication = null;
    updateSelectedLabels();
    renderEventDetail("adminEventDetail", state.selectedEvent);
    renderEventDetail("memberEventDetail", state.selectedEvent);
    fillEventForm(state.selectedEvent);

    renderKnownEventLists();

    $("applicationTable").className = "table-wrap empty-state";
    $("applicationTable").textContent = "선택한 행사의 신청 목록을 조회할 수 있습니다.";
    $("myApplicationBox").className = "detail-box empty-state";
    $("myApplicationBox").textContent = "선택한 행사의 내 신청 상태를 확인할 수 있습니다.";
    showToast(targetId ? "이 도메인에서 조회할 행사를 선택했습니다." : "행사를 선택했습니다.");
}

function renderKnownEventLists() {
    Object.entries(state.eventLists).forEach(([targetId, listState]) => {
        renderEventList(targetId, listState.events, listState.scope);
    });
}

function renderEventDetail(targetId, event) {
    const target = $(targetId);
    target.classList.remove("empty-state");
    target.innerHTML = `
        <div class="card-heading">
            <h3>${escapeHtml(event.title)}</h3>
            ${badge(event.status)}
        </div>
        <dl class="detail-list">
            <div><dt>ID</dt><dd>${event.id}</dd></div>
            <div><dt>장소</dt><dd>${escapeHtml(event.location || "-")}</dd></div>
            <div><dt>일시</dt><dd>${formatDate(event.startAt)} - ${formatDate(event.endAt)}</dd></div>
            <div><dt>정원</dt><dd>${event.capacity ?? "-"}</dd></div>
            <div><dt>설명</dt><dd>${escapeHtml(event.description || "-")}</dd></div>
        </dl>
    `;
}

function fillEventForm(event) {
    $("eventTitle").value = event.title || "";
    $("eventLocation").value = event.location || "";
    $("eventStartAt").value = toInputDateTime(event.startAt);
    $("eventEndAt").value = toInputDateTime(event.endAt);
    $("eventCapacity").value = event.capacity ?? "";
    $("eventStatus").value = event.status || "DRAFT";
    $("eventDescription").value = event.description || "";
}

function newEventForm() {
    state.selectedEvent = null;
    state.selectedApplication = null;
    updateSelectedLabels();
    $("eventTitle").value = "";
    $("eventLocation").value = "";
    $("eventStartAt").value = "";
    $("eventEndAt").value = "";
    $("eventCapacity").value = "30";
    $("eventStatus").value = "DRAFT";
    $("eventDescription").value = "";
    $("adminEventDetail").className = "detail-box empty-state";
    $("adminEventDetail").textContent = "새 행사 정보를 입력한 뒤 생성하세요.";
    showToast("새 행사 입력 상태로 전환했습니다.");
}

function eventBody() {
    return {
        title: $("eventTitle").value,
        description: $("eventDescription").value,
        location: $("eventLocation").value,
        startAt: $("eventStartAt").value,
        endAt: $("eventEndAt").value,
        capacity: Number($("eventCapacity").value)
    };
}

async function createEvent() {
    const payload = await request("POST", "/api/v1/admin/events", eventBody());
    state.selectedEvent = payload.data;
    updateSelectedLabels();
    renderEventDetail("adminEventDetail", state.selectedEvent);
    await loadAdminEvents();
    showToast("행사를 생성했습니다.");
}

async function updateEvent() {
    assertSelectedEvent();
    const payload = await request("PUT", `/api/v1/admin/events/${state.selectedEvent.id}`, eventBody());
    state.selectedEvent = payload.data;
    updateSelectedLabels();
    renderEventDetail("adminEventDetail", state.selectedEvent);
    renderEventDetail("memberEventDetail", state.selectedEvent);
    await loadAdminEvents();
    showToast("행사를 수정했습니다.");
}

async function updateEventStatus(status) {
    try {
        assertSelectedEvent();
        const payload = await request("PATCH", `/api/v1/admin/events/${state.selectedEvent.id}/status`, { status });
        state.selectedEvent = payload.data;
        $("eventStatus").value = status;
        updateSelectedLabels();
        renderEventDetail("adminEventDetail", state.selectedEvent);
        renderEventDetail("memberEventDetail", state.selectedEvent);
        await loadAdminEvents();
        showToast(`행사 상태를 ${status}로 변경했습니다.`);
        setConnection("정상", "success");
    } catch (error) {
        setConnection("오류", "danger");
        showToast(error.message, true);
    }
}

async function deleteEvent() {
    assertSelectedEvent();
    const title = state.selectedEvent.title;
    await request("DELETE", `/api/v1/admin/events/${state.selectedEvent.id}`);
    state.selectedEvent = null;
    state.selectedApplication = null;
    updateSelectedLabels();
    $("adminEventDetail").className = "detail-box empty-state";
    $("adminEventDetail").textContent = "행사가 삭제되었습니다.";
    await loadAdminEvents();
    showToast(`${title} 행사를 삭제했습니다.`);
}

async function loadApplications() {
    assertSelectedEvent();
    const payload = await request("GET", `/api/v1/events/${state.selectedEvent.id}/applications`);
    const applications = payload.data || [];
    $("applicationTable").classList.remove("empty-state");
    $("applicationTable").innerHTML = renderTable(
        ["ID", "이름", "이메일", "상태", "신청일", "처리"],
        applications.map((application) => [
            application.id,
            escapeHtml(application.memberName),
            escapeHtml(application.memberEmail),
            badge(application.status),
            formatDate(application.appliedAt),
            applicationActions(application)
        ]),
        "신청자가 없습니다."
    );
    $("applicationTable").querySelectorAll("[data-application-status]").forEach((button) => {
        button.addEventListener("click", () => updateApplicationStatus(
            Number(button.dataset.applicationId),
            button.dataset.applicationStatus
        ));
    });
    showToast("신청 목록을 불러왔습니다.");
}

function applicationActions(application) {
    return `
        <button type="button" class="secondary" data-application-id="${application.id}" data-application-status="APPROVED">승인</button>
        <button type="button" class="secondary" data-application-id="${application.id}" data-application-status="REJECTED">반려</button>
        <button type="button" class="secondary" data-application-id="${application.id}" data-application-status="CANCELED">취소 처리</button>
    `;
}

async function updateApplicationStatus(applicationId, status) {
    try {
        assertSelectedEvent();
        const payload = await request(
            "PATCH",
            `/api/v1/events/${state.selectedEvent.id}/applications/${applicationId}/status`,
            { status }
        );
        state.selectedApplication = payload.data;
        updateSelectedLabels();
        await loadApplications();
        showToast(`신청 상태를 ${status}로 변경했습니다.`);
        setConnection("정상", "success");
    } catch (error) {
        setConnection("오류", "danger");
        showToast(error.message, true);
    }
}

async function createAttendanceCode() {
    assertSelectedEvent();
    const payload = await request("POST", `/api/v1/admin/events/${state.selectedEvent.id}/attendance-codes`, {});
    const code = payload.data;
    $("attendanceCodeBox").innerHTML = `
        <div class="metric-card">
            <span>행사 ID ${code.eventId}</span>
            <strong>${escapeHtml(code.code)}</strong>
            <span>${formatDate(code.createdAt)}</span>
        </div>
    `;
    showToast("출석 코드를 발급했습니다.");
}

async function loadAttendances() {
    assertSelectedEvent();
    const payload = await request("GET", `/api/v1/events/${state.selectedEvent.id}/attendances`);
    const attendances = payload.data || [];
    $("attendanceTable").classList.remove("empty-state");
    $("attendanceTable").innerHTML = renderTable(
        ["ID", "이름", "이메일", "출석 시각"],
        attendances.map((attendance) => [
            attendance.id,
            escapeHtml(attendance.memberName),
            escapeHtml(attendance.memberEmail),
            formatDate(attendance.attendedAt)
        ]),
        "출석자가 없습니다."
    );
    showToast("출석 현황을 불러왔습니다.");
}

async function loadEventFeedbacks() {
    assertSelectedEvent();
    const payload = await request("GET", `/api/v1/events/${state.selectedEvent.id}/feedbacks`);
    renderFeedbacks(payload.data || []);
    showToast("선택 행사 피드백을 불러왔습니다.");
}

async function loadAllFeedbacks() {
    const payload = await request("GET", "/api/v1/admin/feedbacks");
    renderFeedbacks(payload.data || []);
    showToast("전체 피드백을 불러왔습니다.");
}

function renderFeedbacks(feedbacks) {
    const target = $("feedbackList");
    if (!feedbacks.length) {
        target.className = "feedback-list empty-state";
        target.textContent = "피드백이 없습니다.";
        return;
    }

    target.className = "feedback-list";
    target.innerHTML = feedbacks.map((feedback) => `
        <article class="feedback-item">
            <div class="card-heading">
                <strong>${escapeHtml(feedback.eventTitle)}</strong>
                <span>${escapeHtml(feedback.memberName)} · ${formatDate(feedback.createdAt)}</span>
            </div>
            <p>${escapeHtml(feedback.content)}</p>
        </article>
    `).join("");
}

async function applyEvent() {
    assertSelectedEvent();
    const payload = await request("POST", `/api/v1/events/${state.selectedEvent.id}/applications`, {});
    state.selectedApplication = payload.data;
    updateSelectedLabels();
    renderMyApplication(state.selectedApplication);
    showToast("행사 신청을 완료했습니다.");
}

async function loadMyApplication() {
    assertSelectedEvent();
    const payload = await request("GET", `/api/v1/events/${state.selectedEvent.id}/applications/me`);
    state.selectedApplication = payload.data;
    updateSelectedLabels();
    renderMyApplication(state.selectedApplication);
    showToast("내 신청 상태를 불러왔습니다.");
}

function renderMyApplication(application) {
    const target = $("myApplicationBox");
    target.classList.remove("empty-state");
    target.innerHTML = `
        <div class="card-heading">
            <h3>${escapeHtml(application.eventTitle)}</h3>
            ${badge(application.status)}
        </div>
        <dl class="detail-list">
            <div><dt>신청 ID</dt><dd>${application.id}</dd></div>
            <div><dt>신청자</dt><dd>${escapeHtml(application.memberName)} (${escapeHtml(application.memberEmail)})</dd></div>
            <div><dt>신청일</dt><dd>${formatDate(application.appliedAt)}</dd></div>
        </dl>
    `;
}

async function cancelMyApplication() {
    assertSelectedEvent();
    await request("DELETE", `/api/v1/events/${state.selectedEvent.id}/applications/me`);
    state.selectedApplication = null;
    updateSelectedLabels();
    $("myApplicationBox").className = "detail-box empty-state";
    $("myApplicationBox").textContent = "신청을 취소했습니다.";
    showToast("신청을 취소했습니다.");
}

async function checkAttendance() {
    assertSelectedEvent();
    const payload = await request("POST", `/api/v1/events/${state.selectedEvent.id}/attendances`, {
        code: $("memberAttendanceCode").value
    });
    const attendance = payload.data;
    $("myAttendanceBox").classList.remove("empty-state");
    $("myAttendanceBox").innerHTML = `
        <strong>${escapeHtml(attendance.eventTitle)}</strong>
        <dl class="detail-list">
            <div><dt>출석자</dt><dd>${escapeHtml(attendance.memberName)}</dd></div>
            <div><dt>출석 시각</dt><dd>${formatDate(attendance.attendedAt)}</dd></div>
        </dl>
    `;
    showToast("출석 처리되었습니다.");
}

async function createFeedback() {
    assertSelectedEvent();
    const payload = await request("POST", `/api/v1/events/${state.selectedEvent.id}/feedbacks`, {
        content: $("feedbackContent").value
    });
    const feedback = payload.data;
    $("myFeedbackBox").classList.remove("empty-state");
    $("myFeedbackBox").innerHTML = `
        <strong>${escapeHtml(feedback.eventTitle)}</strong>
        <p>${escapeHtml(feedback.content)}</p>
        <span>${formatDate(feedback.createdAt)}</span>
    `;
    showToast("피드백을 제출했습니다.");
}

function assertSelectedEvent() {
    if (!state.selectedEvent?.id) {
        throw new Error("먼저 행사를 선택하세요.");
    }
}

function updateSelectedLabels() {
    els.selectedEventLabel.textContent = state.selectedEvent
        ? `#${state.selectedEvent.id} ${state.selectedEvent.title}`
        : "선택 안 됨";
    els.selectedApplicationLabel.textContent = state.selectedApplication
        ? `#${state.selectedApplication.id} ${state.selectedApplication.status}`
        : "선택 안 됨";
}

function renderTable(headers, rows, emptyMessage) {
    if (!rows.length) {
        return `<div class="empty-state">${emptyMessage}</div>`;
    }

    const head = headers.map((header) => `<th>${header}</th>`).join("");
    const body = rows.map((row) => `
        <tr>
            ${row.map((cell, index) => `<td class="${index === row.length - 1 && String(cell).includes("<button") ? "actions-cell" : ""}">${cell}</td>`).join("")}
        </tr>
    `).join("");

    return `<table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>`;
}

function badge(value) {
    const key = String(value || "").toLowerCase();
    const className = `pill status-${key}`;
    return `<span class="${className}">${escapeHtml(value || "-")}</span>`;
}

function formatDate(value) {
    if (!value) {
        return "-";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return new Intl.DateTimeFormat("ko-KR", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(date);
}

function toInputDateTime(value) {
    if (!value) {
        return "";
    }
    return String(value).slice(0, 16);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
