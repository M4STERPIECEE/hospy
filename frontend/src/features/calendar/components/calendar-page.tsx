import { useState, useEffect, useCallback } from 'react';
import type { Appointment } from '../../../shared/model';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from 'src/shared/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from 'src/shared/ui/card';
import { Spinner } from 'src/shared/ui/spinner';
import { cn } from 'src/shared/lib/utils';

export const CalendarPage = () => {
    const today = new Date();
    const [currentMonth, setCurrentMonth] = useState(today.getMonth());
    const [currentYear, setCurrentYear] = useState(today.getFullYear());
    const [appointments, setAppointments] = useState<Appointment[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const apiBase = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

    const monthNames = [
        'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
        'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre',
    ];

    const dayNames = ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'];

    const fetchCalendarData = useCallback(async () => {
        setIsLoading(true);
        try {
            const response = await fetch(`${apiBase}/appointments/calendar?year=${currentYear}&month=${currentMonth + 1}`);
            if (!response.ok) throw new Error('Failed to fetch calendar data');
            const data = await response.json();

            const mapped: Appointment[] = data.map((app: {
                id: string;
                user?: { firstName: string; lastName: string; email: string; phone: string };
                service?: { name: string };
                appointmentDate?: string;
                status?: Appointment['status'];
            }) => ({
                id: app.id || '',
                patient: app.user ? `${app.user.firstName} ${app.user.lastName}` : 'Inconnu',
                email: app.user ? app.user.email : '',
                phone: app.user ? app.user.phone : '',
                service: app.service ? app.service.name : 'Service inconnu',
                date: app.appointmentDate ? app.appointmentDate.split('T')[0] : '',
                time: app.appointmentDate ? new Date(app.appointmentDate).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : '',
                status: app.status || 'En attente'
            }));

            setAppointments(mapped);
            setIsLoading(false);
        } catch (error) {
            console.error('Error fetching calendar data:', error);
            setIsLoading(false);
        }
    }, [apiBase, currentMonth, currentYear]);

    useEffect(() => {
        fetchCalendarData();
    }, [fetchCalendarData]);

    const changeMonth = (delta: number) => {
        let newMonth = currentMonth + delta;
        let newYear = currentYear;

        if (newMonth > 11) {
            newMonth = 0;
            newYear++;
        } else if (newMonth < 0) {
            newMonth = 11;
            newYear--;
        }

        setCurrentMonth(newMonth);
        setCurrentYear(newYear);
    };

    const isToday = (day: number) => {
        const d = new Date();
        return (
            day === d.getDate() &&
            currentMonth === d.getMonth() &&
            currentYear === d.getFullYear()
        );
    };

    const generateCalendar = () => {
        const firstDay = new Date(currentYear, currentMonth, 1).getDay();
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

        const days = [];

        dayNames.forEach((day) => {
            days.push(
                <div key={`header-${day}`} className="text-center font-bold text-[0.65rem] p-1.5 text-[var(--primary)] tracking-wide uppercase">
                    {day}
                </div>
            );
        });

        for (let i = 0; i < firstDay; i++) {
            days.push(<div key={`empty-${i}`} />);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dayAppointments = appointments.filter((app) => {
                const appDate = new Date(app.date);
                return (
                    appDate.getDate() === day &&
                    appDate.getMonth() === currentMonth &&
                    appDate.getFullYear() === currentYear
                );
            });

            const hasAppointment = dayAppointments.length > 0;
            const todayCheck = isToday(day);

            days.push(
                <div 
                    key={`day-${day}`} 
                    className={cn(
                        "relative aspect-[1.25] border-2 rounded-lg p-1.5 text-center cursor-pointer transition-all duration-300 flex flex-col justify-between items-center group",
                        todayCheck 
                            ? "border-[var(--accent)] bg-gradient-to-br from-[rgba(5,199,226,0.15)] to-[rgba(5,199,226,0.05)] hover:bg-gradient-to-br hover:from-[rgba(5,199,226,0.15)] hover:to-[rgba(5,199,226,0.08)]" 
                            : hasAppointment 
                                ? "border-[rgba(5,199,226,0.3)] bg-gradient-to-br from-[rgba(5,199,226,0.08)] to-[rgba(5,199,226,0.02)] hover:border-[var(--accent)] hover:bg-gradient-to-br hover:from-[rgba(5,199,226,0.15)] hover:to-[rgba(5,199,226,0.08)]" 
                                : "border-[rgba(10,77,104,0.1)] bg-transparent hover:border-[var(--accent)] hover:bg-gradient-to-br hover:from-[rgba(5,199,226,0.15)] hover:to-[rgba(5,199,226,0.08)]",
                        "hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(5,199,226,0.2)]"
                    )}
                >
                    {todayCheck && (
                        <div className="absolute -top-2 -right-2 bg-[var(--accent)] text-white rounded-full text-[0.65rem] px-2 py-0.5 font-bold hidden sm:block shadow-sm">
                            Aujourd'hui
                        </div>
                    )}
                    <div className={cn(
                        "text-[0.8rem]",
                        todayCheck ? "font-extrabold text-[var(--accent)]" : "font-semibold text-[var(--primary)]"
                    )}>
                        {day}
                    </div>
                    {hasAppointment && (
                        <div className="flex items-center gap-1 mt-2">
                            <div className="w-1.5 h-1.5 rounded-full bg-[var(--accent)]" />
                            <span className="text-[0.55rem] text-[var(--accent)] font-semibold">
                                {dayAppointments.length} RDV
                            </span>
                        </div>
                    )}
                </div>
            );
        }

        return days;
    };

    return (
        <Card className="w-full relative">
            <CardHeader className="flex flex-row items-center justify-between border-b pb-3">
                <div>
                    <CardTitle className="font-poppins text-[1.4rem] mb-1">
                        {monthNames[currentMonth]} {currentYear}
                    </CardTitle>
                    <p className="text-xs text-muted-foreground">
                        Gérez vos rendez-vous
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button 
                        onClick={() => changeMonth(-1)} 
                        variant="outline"
                        size="sm"
                    >
                        <ChevronLeft className="w-4 h-4 mr-1" />
                        Précédent
                    </Button>
                    <Button 
                        onClick={() => changeMonth(1)} 
                        variant="default"
                        size="sm"
                    >
                        Suivant
                        <ChevronRight className="w-4 h-4 ml-1" />
                    </Button>
                </div>
            </CardHeader>
            <CardContent className="pt-4">
                {isLoading ? (
                    <div className="flex justify-center items-center py-20">
                        <Spinner className="h-12 w-12" />
                    </div>
                ) : (
                    <div className="grid grid-cols-7 gap-1.5 relative">
                        {generateCalendar()}
                    </div>
                )}
            </CardContent>
        </Card>
    );
};