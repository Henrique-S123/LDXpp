package proj.env;

import java.util.*;

import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public final class ResourceManager<E> {
    private final class Scope {
        private final Map<String, Binder<E>> live = new HashMap<>();
        private final Set<String> consumed = new HashSet<>();
        private Scope copy() {
            Scope copy = new Scope();
            copy.live.putAll(live);
            copy.consumed.addAll(consumed);
            return copy;
        }
    }

    private final Deque<Scope> scopes = new ArrayDeque<>();

    public ResourceManager() {
        openScope();
    }

    public void openScope() {
        scopes.push(new Scope());;
    }

    public void closeScope() {
        scopes.pop();
    }

    public ResourceManager<E> copy() {
        ResourceManager<E> copy = new ResourceManager<>();
        copy.scopes.clear();
        for (Scope scope : scopes) copy.scopes.addLast(scope.copy());
        return copy;
    }

    public void register(String id, E resource) {
        scopes.peek().live.put(id, new Binder<E>(resource));
        scopes.peek().consumed.remove(id);
    }

    public void register(String id, Binder<E> resource) {
        scopes.peek().live.put(id, resource);
        scopes.peek().consumed.remove(id);
    }

    public boolean contains(String id) {
        for (Scope scope : scopes) if (scope.live.containsKey(id)) return true;
        return false;
    }

    public E consume(String id) throws TypeCheckError {
        for (Scope scope : scopes) {
            Binder<E> resource = scope.live.remove(id);
            if (scope.consumed.contains(id))
                throw new TypeCheckError(ErrorMessages.alreadyUsedLinear(id));
            if (resource != null) {
                scope.consumed.add(id);
                return resource.val;
            }
        }
        return null;
    }

    public String findBinderId(String id) {
        for (Scope scope : scopes) {
            Binder<E> resource = scope.live.get(id);
            if (resource != null) return resource.id;
        }
        return null;
    }

    public Set<String> getUsedLinears() {
        Set<String> result = new HashSet<String>();
        for (Scope scope : scopes) {
            result.addAll(scope.consumed);
        }
        return result;
    }

    public Set<String> getUnusedLinears() {
        Set<String> result = new HashSet<String>();
        for (Scope scope : scopes) {
            result.addAll(scope.live.keySet());
        }
        return result;
    }

    public Set<String> getUnusedScopeLinears() {
        return scopes.peek().live.keySet();
    }
}
