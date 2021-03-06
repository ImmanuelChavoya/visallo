package org.visallo.web;

import com.google.inject.Inject;
import org.visallo.core.bootstrap.InjectHelper;
import org.visallo.core.model.user.UserRepository;
import org.visallo.core.util.JSONUtil;
import org.visallo.core.util.VisalloLogger;
import org.visallo.core.util.VisalloLoggerFactory;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessagingFilter implements PerRequestBroadcastFilter {
    private static final VisalloLogger LOGGER = VisalloLoggerFactory.getLogger(MessagingFilter.class);
    private UserRepository userRepository;


    @Override
    public BroadcastAction filter(String broadcasterId, Object originalMessage, Object message) {
        return new BroadcastAction(message);
    }

    @Override
    public BroadcastAction filter(String broadcasterId, AtmosphereResource r, Object originalMessage, Object message) {
        ensureInitialized();

        try {
            JSONObject json = new JSONObject("" + originalMessage);

            String type = json.optString("type");
            if (type != null && type.equals("setActiveWorkspace")) {
                return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
            }

            JSONArray excludeSessionIds = json.optJSONArray("excludeSessionIds");
            if (excludeSessionIds != null) {
                String currentSessionId = r.getRequest().getSession().getId();
                if (JSONUtil.isInArray(excludeSessionIds, currentSessionId)) {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }

            JSONObject permissionsJson = json.optJSONObject("permissions");
            if (permissionsJson == null) {
                return new BroadcastAction(message);
            }

            JSONArray users = permissionsJson.optJSONArray("users");
            if (users != null) {
                String currentUserId = CurrentUser.getUserId(r.getRequest().getSession());
                if (currentUserId != null && !JSONUtil.isInArray(users, currentUserId)) {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }

            JSONArray sessionIds = permissionsJson.optJSONArray("sessionIds");
            if (sessionIds != null) {
                HttpSession session = r.getRequest().getSession();
                if (session != null && !JSONUtil.isInArray(sessionIds, session.getId())) {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }

            JSONArray workspaces = permissionsJson.optJSONArray("workspaces");
            if (workspaces != null) {
                String currentUserId = CurrentUser.getUserId(r.getRequest().getSession());
                if (!JSONUtil.isInArray(workspaces, userRepository.getCurrentWorkspaceId(currentUserId))) {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }

            return new BroadcastAction(message);
        } catch (JSONException e) {
            LOGGER.error("Failed to filter message:\n" + originalMessage, e);
            return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
        }
    }

    public void ensureInitialized() {
        if (userRepository == null) {
            InjectHelper.inject(this);
            if (userRepository == null) {
                LOGGER.error("userRepository cannot be null");
                checkNotNull(userRepository, "userRepository cannot be null");
            }
        }
    }

    @Inject
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
