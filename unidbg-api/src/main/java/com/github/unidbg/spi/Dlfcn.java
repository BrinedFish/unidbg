package com.github.unidbg.spi;

import com.github.unidbg.Emulator;
import com.github.unidbg.Symbol;
import com.github.unidbg.hook.HookListener;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.pointer.UnicornPointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Dlfcn implements HookListener {

    private static final Log log = LogFactory.getLog(Dlfcn.class);

    protected final UnicornPointer error;

    protected Dlfcn(SvcMemory svcMemory) {
        error = svcMemory.allocate(0x80, "Dlfcn.error");
        assert error != null;
        error.setMemory(0, 0x80, (byte) 0);
    }

    protected final long dlsym(Emulator<?> emulator, long handle, String symbolName) {
        Memory memory = emulator.getMemory();
        Symbol symbol = memory.dlsym(handle, symbolName);
        if (symbol == null) {
            log.info("Find symbol \"" + symbolName + "\" failed: handle=0x" + Long.toHexString(handle) + ", LR=" + emulator.getContext().getLRPointer());
            this.error.setString(0, "Find symbol " + symbolName + " failed");
            Log log = LogFactory.getLog("com.github.unidbg.AbstractEmulator");
            if (log.isDebugEnabled()) {
                emulator.attach().debug();
            }
            return 0;
        }
        return symbol.getAddress();
    }

}
