package com.sony.scalar.webapi.lib.authlib;

/* loaded from: classes.dex */
public enum EnableMethodsState {
    INITIAL { // from class: com.sony.scalar.webapi.lib.authlib.EnableMethodsState.1
        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState accept() {
            return RANDOMHASH_REQUEST;
        }

        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState error() {
            return VERIFY_ERROR;
        }
    },
    RANDOMHASH_REQUEST { // from class: com.sony.scalar.webapi.lib.authlib.EnableMethodsState.2
        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState accept() {
            return AUTHENTICATED;
        }

        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState error() {
            return VERIFY_ERROR;
        }
    },
    VERIFY_ERROR { // from class: com.sony.scalar.webapi.lib.authlib.EnableMethodsState.3
        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState accept() {
            return INITIAL;
        }
    },
    AUTHENTICATED { // from class: com.sony.scalar.webapi.lib.authlib.EnableMethodsState.4
        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState accept() {
            return RANDOMHASH_REQUEST;
        }

        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsState
        public EnableMethodsState error() {
            return VERIFY_ERROR;
        }
    };

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static EnableMethodsState[] valuesCustom() {
        EnableMethodsState[] valuesCustom = values();
        int length = valuesCustom.length;
        EnableMethodsState[] enableMethodsStateArr = new EnableMethodsState[length];
        System.arraycopy(valuesCustom, 0, enableMethodsStateArr, 0, length);
        return enableMethodsStateArr;
    }

    /* synthetic */ EnableMethodsState(EnableMethodsState enableMethodsState) {
        this();
    }

    public EnableMethodsState accept() {
        throw new IllegalStateException();
    }

    public EnableMethodsState error() {
        throw new IllegalStateException();
    }
}
