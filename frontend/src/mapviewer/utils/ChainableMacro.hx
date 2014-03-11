/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mapviewer.utils;

import haxe.macro.Expr.ExprDef;
import haxe.macro.TypeTools;
import haxe.macro.Expr;
import haxe.macro.Context;
import haxe.macro.Type;

class ChainableMacro {

    public static function build() {
        var fields : Array<Field> = Context.getBuildFields();
        var localType : Type = Context.getLocalType();
        var ownerType : ComplexType = TypeTools.toComplexType(localType);
        var tPath : TypePath;
        switch (ownerType) {
            case TPath(p):
                tPath = p;
            default:
        }
        var out = [];
        var builder : Array<Field> = [];
		var builderName = (tPath.sub == null ? tPath.name : tPath.sub);

        for (field in fields) {
            out.push(field);
            var ok = false;
            for (meta in field.meta) {
                if (meta.name == ":chain") {
                    ok = true;
                    break;
                }
            }
            if (!ok) continue;
            switch (field.kind) {
                case FieldType.FVar(t, e):
                    var chain = {
                        name: field.name,
                        kind: FieldType.FFun({
                            args: [{
                                name: field.name,
                                opt: false,
                                type: t
                            }],
                            ret: TPath({
                                pack: tPath.pack,
                                name: builderName + "$Builder",
                                params: []
                            }),
                            expr: { expr: ExprDef.EBlock([
                                { expr: ExprDef.EBinop(Binop.OpAssign,
                                    { expr: ExprDef.EField(
                                        { expr: ExprDef.EConst(Constant.CIdent('owner')), pos: field.pos }
                                        , field.name), pos: field.pos },
                                    { expr: ExprDef.EConst(Constant.CIdent(field.name)), pos: field.pos }
                                ), pos: field.pos },
                                { expr: ExprDef.EReturn({ expr: ExprDef.EConst(Constant.CIdent("this")), pos: field.pos }), pos: field.pos }
                            ]), pos: field.pos},
                            params: []
                        }),
                        pos: field.pos,
                        access: [Access.APublic]
                    };
                    field.access.push(Access.APublic);
                    builder.push(chain);
                default:
            }
        }
        builder.push({name: "owner", kind: FieldType.FVar(ownerType), pos: Context.currentPos(), access: [Access.APrivate]});
        builder.push({
            name: "new",
            kind: FieldType.FFun({
                args: [{name: "o", opt: false, type: ownerType}],
                ret: null,
                expr: { expr: ExprDef.EBlock([
                    { expr: ExprDef.EBinop(Binop.OpAssign,
                        { expr: ExprDef.EConst(Constant.CIdent('owner')),  pos: Context.currentPos() },
                        { expr: ExprDef.EConst(Constant.CIdent('o')), pos: Context.currentPos() }
                    ), pos: Context.currentPos() }
                ]), pos: Context.currentPos() },
                params: []
            }),
            pos: Context.currentPos(),
            access: [Access.APublic]
        });
        builder.push({
            name: "ret",
            kind: FieldType.FFun({
                args: [],
                ret: ownerType,
                expr: { expr: ExprDef.EBlock([
                    { expr: ExprDef.EReturn({ expr: ExprDef.EConst(Constant.CIdent("owner")), pos: Context.currentPos() }), pos: Context.currentPos() }
                    ]), pos: Context.currentPos() },
                params: []
            }),
            pos: Context.currentPos(),
            access: [Access.APublic]
        });
        out.push({
            name: "chain" + builderName,
            kind: FieldType.FFun({
                args: [],
                ret:  TPath({
                    pack: tPath.pack,
                    name: builderName + "$Builder",
                    params: []
                }),
                expr: { expr: ExprDef.EBlock([
                    { expr: ExprDef.EReturn({ expr:
                        ExprDef.ENew({
                                pack: tPath.pack,
                                name: builderName + "$Builder",
                                params: []
                            },
                            [{ expr: ExprDef.EConst(Constant.CIdent("this")), pos: Context.currentPos() }]
                        ),
                        pos: Context.currentPos() }),
                    pos: Context.currentPos() }
                ]), pos: Context.currentPos() },
                params: []
            }),
            pos: Context.currentPos(),
            access: [Access.APublic]
        });
        Context.defineType({
            pack: tPath.pack,
            name: builderName + "$Builder",
            pos: Context.currentPos(),
            meta: [],
            params: [],
            isExtern: false,
            kind: TypeDefKind.TDClass(),
            fields: builder
        });
        return out;
    }
}