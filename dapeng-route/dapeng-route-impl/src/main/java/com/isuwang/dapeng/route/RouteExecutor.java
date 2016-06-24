package com.isuwang.dapeng.route;


import com.isuwang.dapeng.core.InvocationContext;
import com.isuwang.dapeng.route.pattern.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eric
 * @date
 */
public class RouteExecutor {


    /**
     * 通过请求上下文，规则列表，当前服务ip，判断该请求是否可以访问此ip
     *
     * @param ctx    上下文
     * @param routes 规则列表
     * @param server 服务器ip地址
     * @return
     */
    public static boolean isServerMatched(InvocationContext ctx, List<Route> routes, InetAddress server) {

        //todo 这里要考虑一个规则冲突的问题，如果在某一个规则中可以访问，在另一个规则中不能访问，怎么处理?
        boolean matchOne = false;
        boolean result = false;
        for (Route route : routes) {
            boolean isMatched = checkRouteCondition(ctx, route.getLeft());
            if (isMatched) {
                matchOne = true;
                if (route.getRight() instanceof IpPattern) {
                    result = matched(server, (IpPattern) route.getRight());
                } else if (route.getRight() instanceof NotPattern) {
                    result = !matched(server, (IpPattern) ((NotPattern) route.getRight()).getPattern());
                } else {
                    throw new AssertionError("route right must be IpPattern or ~IpPattern");
                }
            }
        }
        if (matchOne)
            return result;
        return true;
    }


    /**
     * 通过请求上下文，规则，以及备选的服务器ip列表，获取该请求可以访问的ip列表
     *
     * @param ctx     上下文
     * @param routes  规则列表
     * @param servers 备选的服务器ip列表
     * @return
     */
    public static Set<InetAddress> execute(InvocationContext ctx, List<Route> routes, List<String> servers) {
        Set added = new HashSet<InetAddress>();// 匹配的服务器
        Set removed = new HashSet<InetAddress>();// 拒绝的服务器，对应于 ~ip"" 模式

        for (Route route : routes) {
            boolean isMatched = checkRouteCondition(ctx, route.getLeft());
            if (isMatched) {
                Pattern right = route.getRight();
                if (right instanceof IpPattern) {
                    added.addAll(filterServer(servers, right));
                } else if (right instanceof NotPattern) {
                    removed.addAll(filterServer(servers, right));
                } else {
                    throw new AssertionError("route right must be IpPattern or ~IpPattern");
                }
            }
        }
        //如果没有可匹配的服务，则匹配规则失效， 将服务列表全部作为信任服务
        if (added.isEmpty()) {
            for (String server : servers) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(server);
                    added.add(inetAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        added.removeAll(removed);
        return added;
    }


    /**
     * 返回满足Pattern条件的服务ip列表
     *
     * @param servers
     * @param right
     * @return
     */
    public static List<InetAddress> filterServer(List<String> servers, Pattern right) {
        List<InetAddress> inetAddresses = new ArrayList<>();
        for (String server : servers) {
            try {
                InetAddress inetAddress = InetAddress.getByName(server);
                if (matched(inetAddress, (IpPattern) (right instanceof NotPattern ? ((NotPattern) right).getPattern() : right))) {
                    inetAddresses.add(inetAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inetAddresses;
    }

    public static boolean checkRouteCondition(InvocationContext ctx, MatchLeftSide left) {
        boolean result = false;
        if (left instanceof OtherWise) {
            return result;//// TODO: 2016/6/22 OtherWise的定义再仔细考虑一下 
        } else if (left instanceof Matchers) {
            List<Matcher> matchers = ((Matchers) left).getMatchers();
            for (Matcher matcher : matchers) {
                Object value = checkFieldMatcher(ctx, matcher);
                if (value != null) {
                    List<Pattern> patterns = matcher.getPatterns();
                    for (Pattern pattern : patterns) {
                        result = matched(pattern, value);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据matcher.Id，返回上下文中对应的值
     *
     * @param ctx
     * @param matcher
     * @return
     */
    public static Object checkFieldMatcher(InvocationContext ctx, Matcher matcher) {
        Id id = matcher.getId();
        if ("operatorId".equals(id.getName())) {
            return ctx.getHeader().getOperatorId().orElse(null);
        } else if ("callerFrom".equals(id.getName())) {
            return ctx.getHeader().getCallerFrom().orElse(null);
        } else if ("ip".equals(id.getName())) {
            return ctx.getHeader().getCallerIp().orElse(null);
        } else if ("methodName".equals(id.getName())) {
            throw new AssertionError("not support methodName");
        } else {
            throw new AssertionError("not support Field");
        }
    }


    public static boolean matched(Pattern pattern, Object value) {
        if (pattern instanceof NotPattern) {
            return !(matched(((NotPattern) pattern).getPattern(), value));
        } else if (pattern instanceof ModPattern) {
            return matched(Long.valueOf(value.toString()), (ModPattern) pattern);
        } else if (pattern instanceof StringPattern) {
            return matched((String) value, (StringPattern) pattern);
        } else if (pattern instanceof RegexpPattern) {
            return ((String) value).matches(((RegexpPattern) pattern).getValue());
        } else if (pattern instanceof NumberPattern) {
            return matched(Long.valueOf(value.toString()), (NumberPattern) pattern);
        } else if (pattern instanceof RangePattern) {
            return Long.valueOf(value.toString()) > ((RangePattern) pattern).getLow() && Long.valueOf(value.toString()) <= ((RangePattern) pattern).getHigh();
        } else if (pattern instanceof IpPattern) {
            try {
                return matched(InetAddress.getByName((String) value), (IpPattern) pattern);
            } catch (Exception e) {
                System.out.print(e.getLocalizedMessage());
            }
        }
        return false;
    }

    public static boolean matched(String str, StringPattern stringPattern) {
        boolean isMatch = false;
        for (String temp : stringPattern.getValue()) {
            if (str.equals(temp)) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }

    public static boolean matched(Long num, ModPattern modPattern) {
        Long remain = num % modPattern.getBase();
        return remain >= modPattern.getRemain().getLow() && remain <= modPattern.getRemain().getHigh();
    }

    public static boolean matched(Long num, NumberPattern numberPattern) {
        boolean isMatch = false;
        for (Long temp : numberPattern.getValue()) {
            if (temp == num) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }

    /**
     * 判断ip是否符合IpPattern
     *
     * @param address
     * @param ipPattern
     * @return
     */
    public static boolean matched(InetAddress address, IpPattern ipPattern) {
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(ipPattern.getIp());
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.getLocalizedMessage());
            return false;
        }
        byte[] bytes = ip.getAddress();
        int ipInt = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF));
        int mask2 = 32 - ipPattern.getMask();  // 8
        int mask2Flag = (1 << mask2) - 1;
        int mask1Flag = -1 & (~mask2Flag);

        byte[] addressBytes = address.getAddress();
        int addressInt = ((addressBytes[0] & 0xFF) << 24) | ((addressBytes[1] & 0xFF) << 16) | ((addressBytes[2] & 0xFF) << 8) | ((addressBytes[3] & 0xFF));

        return (addressInt & mask1Flag) == (ipInt & mask1Flag);
    }
}
