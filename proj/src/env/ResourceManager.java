package proj.src.env;

import proj.src.errors.*;
import proj.src.types.ASTType;

import java.util.*;

public final class ResourceManager {
    ResourceManager anc;
    Map<String, Binder<ASTType>> live;
    Set<String> consumed;

    public ResourceManager() {
        anc = null;
        live = new HashMap<>();
        consumed = new HashSet<>();
    }

    public ResourceManager(ResourceManager ancestor) {
        anc = ancestor;
        live = new HashMap<>();
        consumed = new HashSet<>();
    }

    public ResourceManager openScope() { return new ResourceManager(this); }

    public ResourceManager closeScope() { return anc; }

    public ResourceManager copy() {
        ResourceManager c = new ResourceManager(this.anc == null ? null : this.anc.copy());
        c.live = new HashMap<>(live);
        c.consumed = new HashSet<>(consumed);
        return c;
    }

    public void register(String id, Binder<ASTType> resource) {
        live.put(id, resource);
        consumed.remove(id);
    }

    private Binder<ASTType> findBinder(String id) {
        ResourceManager curr = this;
        while (curr != null) {
            Binder<ASTType> b = curr.live.get(id);
            if (b != null) return b;
            curr = curr.anc;
        }
        return null;
    }

    public boolean contains(String id) {
        return findBinder(id) != null;
    }

    public String findBinderId(String id) {
        Binder<ASTType> b = findBinder(id);
        return b == null ? null : b.id;
    }

    public ASTType consume(String id) throws TypeCheckError {
        ResourceManager curr = this;
        while (curr != null) {
            Binder<ASTType> resource = curr.live.remove(id);
            if (curr.consumed.contains(id))
                throw new TypeCheckError(ErrorMessages.alreadyUsedLinear(id));
            if (resource != null) {
                curr.consumed.add(id);
                return resource.val;
            }
            curr = curr.anc;
        }
        return null;
    }

    public Set<String> getUsedLinears() {
        Set<String> result = new HashSet<String>();
        ResourceManager curr = this;
        while (curr != null) {
            result.addAll(curr.consumed);
            curr = curr.anc;
        }
        return result;
    }

    public Set<String> getUnusedLinears() {
        Set<String> result = new HashSet<String>();
        ResourceManager curr = this;
        while (curr != null) {
            result.addAll(curr.live.keySet());
            curr = curr.anc;
        }
        return result;
    }

    public Set<String> getUnusedScopeLinears() {
        return live.keySet();
    }
}
