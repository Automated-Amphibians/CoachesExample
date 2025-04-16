package org.aa8426.lib.dashboard;

import java.util.Map;
import java.util.Map.Entry;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.*;

import org.aa8426.lib.Utils.Triple;

import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class SendableFluent {    

    public interface ISendableFluent {
        public SendableFluent addSendables(SendableFluent s);
    }

    static private SendableFluent instance;

    public Map<String, SendableChooser<String>> choosers = new HashMap<>();
    public List<Sendable> sendables = new ArrayList<>();
    Map<String, Triple<Supplier<?>, Consumer<?>, String>> props = new HashMap<>();
    private List<String> keys = new ArrayList<String>();
    private String defaultKey = "";    
    public boolean debugMode = true;

    public static SendableFluent getInstance() {
        if (instance == null) {
            instance = new SendableFluent();
        }
        return instance;
    }

    public SendableFluent() {          
        //addDefaultKey(Constants.ROBOT_NAME); -- see SendableRegistry.add(this, Constants.ROBOT_NAME); in the MySendable nested class for how the root context is provided.
    }

    public SendableFluent addDefaultKey(String key) {
        keys.add(key);        
        updateDefaultKey();
        return this;
    }

    private SendableFluent updateDefaultKey() {
        if (keys.size() == 0) {
            defaultKey = "";
            return this;
        }
        defaultKey = String.join("/", keys) + "/";
        return this;        
    }

    public SendableFluent removeDefaultKey() {
        if (keys.size() == 0) {
            return this;
        }
        keys.remove(keys.size()-1);
        updateDefaultKey();
        return this;
    }

    public SendableFluent setKeys(String... key) {
        keys.clear();
        keys.addAll(List.of(key));
        updateDefaultKey();
        return this;
    }
    
    public SendableFluent addDouble(String key, DoubleSupplier get, DoubleConsumer apply) {        
        add(key, get == null ? null : get::getAsDouble, apply == null ? null : apply::accept, "__DOUBLE");
        return this;
    }

    // public SendableFluent addDouble(String key, Supplier<Double> get, Consumer<Double> apply) {
    //     return add(key, get, apply, "__DOUBLE");
    // }

    public SendableFluent addString(String key, Supplier<String> get, Consumer<String> apply) {
        return add(key, get, apply, "__STRING");
    }

    // public SendableFluent addBoolean(String key, Supplier<Boolean> get, Consumer<Boolean> apply) {
    //     return add(key, get, apply, "__BOOLEAN");
    // }

    public SendableFluent addBoolean(String key, BooleanSupplier get, BooleanConsumer apply) {
        return add(key, get == null ? null : get::getAsBoolean, apply == null ? null : apply::accept, "__BOOLEAN");
    }    

    public SendableFluent addInteger(String key, LongSupplier get, LongConsumer apply) {        
        return add(key, get == null ? null : get::getAsLong, apply == null ? null : apply::accept, "__INTEGER");        
    }

    // public SendableFluent addInteger(String key, Supplier<Long> get, Consumer<Long> apply) {
    //     return add(key, get, apply, "__INTEGER");
    // }

    public SendableFluent addRaw(String key, Supplier<byte[]> get, Consumer<byte[]> apply, String type) {
        return add(key, get, apply, type);
    }

    private <T> SendableFluent add(String key, Supplier<T> get, Consumer<T> apply, String type) {        
        props.put(defaultKey + key, Triple.of(get, apply, type));
        return this;
    }

    public SendableChooser<String> addChooseable(String key, String[] options, String _default) {
        SendableChooser<String> chooser = new SendableChooser<>();
        if (options.length == 0) {
            return chooser;
        }
        if (_default == null) {
            _default = options[0];
        }
        for(String option:options) {
            if (option == null) {
                continue;
            }
            if (option.equals(_default)) {
                chooser.setDefaultOption(option, option);
            } else {
                chooser.addOption(option, option);
            }
        }        
        choosers.put(defaultKey + key, chooser);
        return chooser;
    }

    public SendableChooser<Boolean> addChooseable(String key, Boolean def) {
        SendableChooser<Boolean> chooser = new SendableChooser<>();       
        return chooser;
    }
    
    static public class MySendable implements Sendable {

        Map<String, Triple<Supplier<?>, Consumer<?>, String>> props;
        String rootId;

        public MySendable(Map<String, Triple<Supplier<?>, Consumer<?>, String>> props, String rootId) {            
            this.props = props;
            //SendableRegistry.add(this, Constants.ROBOT_NAME);
            this.rootId = rootId;
            SendableRegistry.add(this, rootId);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initSendable(SendableBuilder builder) {
            builder.setSmartDashboardType("CustomType");                                
            for(Entry<String, Triple<Supplier<?>, Consumer<?>, String>> e:props.entrySet()) {
                Supplier<?> get = e.getValue().getFirst();
                Consumer<?> apply = e.getValue().getSecond();                    
                String type = e.getValue().getThird();
                if (e.getKey().toUpperCase().contains("PHOTON")) {
                    System.out.println("adding... "+e.getKey());
                }
                switch(type) {
                    case "__DOUBLE":
                        DoubleSupplier ds = get == null ? null : () -> ((Supplier<Double>)get).get();
                        DoubleConsumer dc = apply == null ? null : (v) -> ((Consumer<Double>)apply).accept(v);                            
                        builder.addDoubleProperty(e.getKey(), ds, dc);                        
                        continue;
                    case "__STRING":
                        builder.addStringProperty(e.getKey(), (Supplier<String>)get, (Consumer<String>)apply);                        
                        continue;
                    case "__INTEGER":
                        LongSupplier ls = get == null ? null : () -> ((Supplier<Long>)get).get();
                        LongConsumer lc = apply == null ? null : (v) -> ((Consumer<Long>)apply).accept(v);
                        builder.addIntegerProperty(e.getKey(), ls, lc);
                        continue;
                    case "__BOOLEAN":
                        BooleanSupplier bs = get == null ? null : () -> ((Supplier<Boolean>)get).get();
                        BooleanConsumer bc = apply == null ? null : (v) -> ((Consumer<Boolean>)apply).accept(v);
                        builder.addBooleanProperty(e.getKey(), bs, bc);
                        continue;
                    default: // hopefully a raw specified by the user.
                        builder.addRawProperty(e.getKey(), type, (Supplier<byte[]>)get, (Consumer<byte[]>)apply);
                        continue;                        
                }                    
            }
        }
    } 

    public SendableFluent get(String rootId) {          
        MySendable s = new MySendable(props, rootId);
        SmartDashboard.putData(s);
        sendables.add(s);        
        props.clear();
        for(Entry<String, SendableChooser<String>> e:this.choosers.entrySet()) {
            SmartDashboard.putData(e.getKey(), e.getValue());
        }
        return this;
    }

    public <E extends Enum<E>> SendableChooser<String> addChooseable(String key, Class<E> anEnum, String _default) {        
        List<String> vals = new ArrayList<>();
        for (E value : anEnum.getEnumConstants()) {
            vals.add(value.name());
        }
        return addChooseable(key, vals.toArray(new String[] {}), _default);
    }

    public String getCurrentKey() {
        return defaultKey;
    }

}
